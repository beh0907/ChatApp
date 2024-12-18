package com.skymilk.chatapp.store.data.repository

import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.oAuthCredential
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.messaging.FirebaseMessaging
import com.skymilk.chatapp.store.data.dto.User
import com.skymilk.chatapp.store.data.dto.toUser
import com.skymilk.chatapp.store.data.utils.Constants
import com.skymilk.chatapp.store.domain.repository.AuthRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.math.abs

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseFireStore: FirebaseFirestore,
    private val firebaseMessaging: FirebaseMessaging,
) : AuthRepository {

    private var authListener: ListenerRegistration? = null

    override fun signInWithGoogle(idToken: String): Flow<User> = flow {
        val firebaseCredential =
            GoogleAuthProvider.getCredential(idToken, null)
        val authResult = firebaseAuth.signInWithCredential(firebaseCredential).await()
        val authUser = authResult.user?.toUser() ?: throw IllegalStateException("유저 정보가 없습니다.")

        //구글 회원가입이라면
        if (isFirstTimeLogin(authResult)) saveUserToDatabase(authUser)

        emitAll(getUserFlow(authUser.id))
    }

    override fun signInWithKakao(idToken: String, accessToken: String): Flow<User> = flow {
        val kakaoIdTokenCredential = oAuthCredential("oidc.kakao") {
            this.idToken = idToken
            this.accessToken = accessToken
        }

        // Firebase 커스텀 토큰을 사용하여 Firebase 로그인 처리
        val authResult = firebaseAuth.signInWithCredential(kakaoIdTokenCredential).await()
        val authUser = authResult.user?.toUser() ?: throw IllegalStateException("유저 정보가 없습니다.")

        // 최초 로그인 시 데이터베이스에 유저 정보 저장
        if (isFirstTimeLogin(authResult)) saveUserToDatabase(authUser)

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

        if (userId != null) emitAll(getUserFlow(userId))
    }

    // Firebase Firestore에서 유저 정보를 Flow로 가져오는 함수
    private fun getUserFlow(userId: String): Flow<User> = callbackFlow {
        authListener = firebaseFireStore.collection(Constants.FirebaseReferences.USERS)
            .document(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close()
                    return@addSnapshotListener
                }

                snapshot?.toObject(User::class.java)?.let { user ->
                    //유저 정보 전달
                    trySend(user)

                    //FCM 토큰 업데이트
                    launch {
                        try {
                            // FCM 토큰 가져오기 시도
                            val fcmToken = firebaseMessaging.token.await()

                            if (fcmToken != user.fcmToken) {
                                firebaseFireStore.collection(Constants.FirebaseReferences.USERS)
                                    .document(userId)
                                    .update("fcmToken", fcmToken)
                                    .await()
                            }
                        } catch (e: Exception) {
                            // 토큰 가져오기 실패 시 원래 유저 객체 사용 (fcmToken은 null 또는 빈 문자열)
                            println("FCM 토큰 가져오기 실패: ${e.message}")
                        }
                    }
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
    suspend fun saveUserToDatabase(user: User) {
        // 업데이트된 User 객체를 데이터베이스에 저장
        firebaseFireStore.collection(Constants.FirebaseReferences.USERS)
            .document(user.id)
            .set(user)
            .await()
    }

    //최초 로그인(회원가입 여부 판단)
    private fun isFirstTimeLogin(authResult: AuthResult): Boolean {
        val lastSignInTime = authResult.user?.metadata?.lastSignInTimestamp ?: return false
        val creationTime = authResult.user?.metadata?.creationTimestamp ?: return false
        return abs(lastSignInTime - creationTime) < 1000 // 1초 이내라면 회원가입으로 판단
    }
}