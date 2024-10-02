package com.skymilk.chatapp.store.data.repository

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.oAuthCredential
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.messaging.FirebaseMessaging
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.skymilk.chatapp.BuildConfig
import com.skymilk.chatapp.store.domain.model.User
import com.skymilk.chatapp.store.domain.model.toUser
import com.skymilk.chatapp.store.domain.repository.AuthRepository
import com.skymilk.chatapp.utils.FirebaseUtil
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.math.abs

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseFireStore: FirebaseFirestore,
    private val firebaseMessaging: FirebaseMessaging,
    private val kakaoUserApiClient: UserApiClient,
    @ApplicationContext private val context: Context
) : AuthRepository {

    private var authListener: ListenerRegistration? = null

    override fun signInWithGoogle(): Flow<User> = flow {
        val googleIdTokenCredential = getGoogleIdTokenCredential()
        val firebaseCredential =
            GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)
        val authResult = firebaseAuth.signInWithCredential(firebaseCredential).await()
        val authUser = authResult.user?.toUser() ?: throw IllegalStateException("유저 정보가 없습니다.")

        //구글 회원가입이라면
        if (isFirstTimeLogin(authResult)) {
            saveUserToDatabase(authUser)
        }

        emitAll(getUserFlow(authUser.id))
    }

    override fun signInWithKakao(activity: Activity): Flow<User> = flow {
        // Kakao SDK에서 액세스 토큰을 가져옴
        val kakaoToken = getKakaoToken(activity)

        val firebaseCredential = oAuthCredential("oidc.kakao") {
            idToken = kakaoToken?.idToken
            accessToken = kakaoToken?.accessToken
        }

        // Firebase 커스텀 토큰을 사용하여 Firebase 로그인 처리
        val authResult = firebaseAuth.signInWithCredential(firebaseCredential).await()
        val authUser = authResult.user?.toUser() ?: throw IllegalStateException("유저 정보가 없습니다.")

        // 최초 로그인 시 데이터베이스에 유저 정보 저장
        if (isFirstTimeLogin(authResult)) {
            saveUserToDatabase(authUser)
        }

        emitAll(getUserFlow(authUser.id))
    }

    //아이디 / 이메일 로그인
    override fun signInWithEmailAndPassword(
        email: String,
        password: String
    ): Flow<User> = flow {
        val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
        val userId = result.user?.uid ?: throw IllegalStateException("로그인에 실패하였습니다")

        emitAll(getUserFlow(userId))
    }

    //아이디/이메일 회원가입 처리
    override fun signUpWithEmailAndPassword(
        name: String,
        email: String,
        password: String
    ): Flow<User> = flow {
        val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
        val user = result.user?.toUser()?.apply { username = name }
            ?: throw IllegalStateException("회원 가입에 실패하였습니다")

        //회원가입 정보 저장
        saveUserToDatabase(user)

        emitAll(getUserFlow(user.id))
    }

    //현재 인증된 유저정보 가져오기
    override fun getCurrentUser(): Flow<User> = flow {
        val userId = firebaseAuth.currentUser?.uid

        if (userId != null) {
            emitAll(getUserFlow(userId))
        }
    }

    // Firebase Firestore에서 유저 정보를 Flow로 가져오는 함수
    private fun getUserFlow(userId: String): Flow<User> = callbackFlow {
        authListener = firebaseFireStore.collection("users")
            .document(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                snapshot?.toObject(User::class.java)?.let { updatedUser ->
                    trySend(updatedUser)
                }
            }
        awaitClose { authListener?.remove() }
    }

    //로그아웃
    override suspend fun signOut() {
        //로그아웃
        firebaseAuth.signOut()

        //실시간 내 정보 가져오는 리스너 제거
        authListener?.remove()
    }

    //회원 가입 시 유저 정보 DB 등록
    override suspend fun saveUserToDatabase(user: User) {
        val updatedUser = try {
            // FCM 토큰 가져오기 시도
            val fcmToken = firebaseMessaging.token.await()
            user.copy(fcmToken = fcmToken)
        } catch (e: Exception) {
            // 토큰 가져오기 실패 시 원래 유저 객체 사용 (fcmToken은 null 또는 빈 문자열)
            println("FCM 토큰 가져오기 실패: ${e.message}")
            user
        }

        // 업데이트된 User 객체를 데이터베이스에 저장
        firebaseFireStore.collection("users")
            .document(updatedUser.id)
            .set(updatedUser)
            .await()
    }

    //구글 인증 토큰 가져오기
    private suspend fun getGoogleIdTokenCredential(): GoogleIdTokenCredential {
        val credentialManager = CredentialManager.create(context)
        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(BuildConfig.GOOGLE_AUTH_WEB_CLIENT_ID)
            .build()
        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()
        val result = credentialManager.getCredential(context, request)
        return GoogleIdTokenCredential.createFrom(result.credential.data)
    }

    private suspend fun getKakaoToken(activity: Activity): OAuthToken? = suspendCancellableCoroutine { continuation ->
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                Log.d("getKakaoToken", "error - 카카오 로그인 실패")
                continuation.resumeWithException(error)
            } else if (token != null) {
                Log.d("getKakaoToken", "token 카카오 로그인 성공")
                continuation.resume(token)
            } else {
                Log.d("getKakaoToken", "else - 카카오 로그인 실패")
                continuation.resumeWithException(IllegalStateException("카카오 로그인 실패"))
            }
        }
        Log.d("getKakaoToken", "카톡 설치 여부 : ${kakaoUserApiClient.isKakaoTalkLoginAvailable(activity)}")

        if (kakaoUserApiClient.isKakaoTalkLoginAvailable(activity)) {
            kakaoUserApiClient.loginWithKakaoTalk(activity) { token, error ->

                Log.d("getKakaoToken", "token : ${token.toString()}")
                Log.d("getKakaoToken", "error : ${error?.message.toString()}")

                if (error != null) {
                    // 사용자가 카카오톡 설치 후 디바이스 권한 요청 화면에서 로그인을 취소한 경우,
                    // 의도적인 로그인 취소로 보고 카카오계정으로 로그인 시도 없이 로그인 취소로 처리 (예: 뒤로 가기)
                    if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                        return@loginWithKakaoTalk
                    }
                    // 카카오톡에 연결된 카카오계정이 없는 경우, 카카오계정으로 로그인 시도
                    UserApiClient.instance.loginWithKakaoAccount(activity, callback = callback)
                } else if (token != null) {
                    continuation.resume(token)
                }
            }
        } else {
            kakaoUserApiClient.loginWithKakaoAccount(activity, callback = callback)
        }
    }

    //최초 로그인(회원가입 여부 판단)
    private fun isFirstTimeLogin(authResult: AuthResult): Boolean {
        val lastSignInTime = authResult.user?.metadata?.lastSignInTimestamp ?: return false
        val creationTime = authResult.user?.metadata?.creationTimestamp ?: return false
        return abs(lastSignInTime - creationTime) < 1000 // 1초 이내라면 회원가입으로 판단
    }

    //오류 체크
    private fun handleAuthError(e: Exception): Result<User> {
        e.printStackTrace()
        val errorMessage = when (e) {
            is FirebaseAuthException -> FirebaseUtil.getErrorMessage(e)
            else -> e.message ?: "An unknown error occurred"
        }
        return Result.failure(Exception(errorMessage))
    }
}