package com.skymilk.chatapp.store.data.repository

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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
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
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.math.abs

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseFireStore: FirebaseFirestore,
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

    override suspend fun saveUserToDatabase(user: User) {
        firebaseFireStore.collection("users")
            .document(user.id)
            .set(user)
            .await()
    }

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