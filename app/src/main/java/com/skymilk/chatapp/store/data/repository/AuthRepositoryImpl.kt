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
import com.skymilk.chatapp.BuildConfig
import com.skymilk.chatapp.store.domain.model.User
import com.skymilk.chatapp.store.domain.model.toUser
import com.skymilk.chatapp.store.domain.repository.AuthRepository
import com.skymilk.chatapp.utils.FirebaseUtil
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.math.abs

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseFireStore: FirebaseFirestore,
    @ApplicationContext private val context: Context
) : AuthRepository {

    override suspend fun signInWithGoogle(): Result<User> = try {
        val googleIdTokenCredential = getGoogleIdTokenCredential()
        val firebaseCredential = GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)
        val authResult = firebaseAuth.signInWithCredential(firebaseCredential).await()
        val authUser = authResult.user?.toUser() ?: throw IllegalStateException("유저 정보가 없습니다.")

        //구글 회원가입이라면
        if (isFirstTimeLogin(authResult)) {
            saveUserToDatabase(authUser)
            Result.success(authUser)
        } else {
            //이미 저장된 회원이라면
            val document = firebaseFireStore.collection("users").document(authUser.id).get().await()
            val user = document.toObject(User::class.java)

            Result.success(user ?: throw IllegalStateException("유저 정보가 없습니다."))
        }

    } catch (e: Exception) {
        handleAuthError(e)
    }

    //아이디 / 이메일 로그인
    override suspend fun signInWithEmailAndPassword(
        email: String,
        password: String
    ): Result<User> = try {
        val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
        val document = firebaseFireStore.collection("users").document(result.user?.uid!!).get().await()
        val user = document.toObject(User::class.java)

        Result.success(user ?: throw IllegalStateException("유저 정보가 없습니다."))
    } catch (e: Exception) {
        handleAuthError(e)
    }

    //아이디/이메일 회원가입 처리
    override suspend fun signUpWithEmailAndPassword(
        name: String,
        email: String,
        password: String
    ): Result<User> = try {
        val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
        val user = result.user?.toUser()?.apply { username = name }
            ?: throw IllegalStateException("유저 정보가 없습니다.")

        saveUserToDatabase(user)

        Result.success(user)
    } catch (e: Exception) {
        handleAuthError(e)
    }

    //현재 인증된 유저정보 가져오기
    override suspend fun getCurrentUser(): User? = runCatching {
        val document = firebaseFireStore.collection("users").document(firebaseAuth.currentUser?.uid!!).get().await()
        document.toObject(User::class.java)
    }.getOrElse {
        null
    }

    //로그아웃
    override suspend fun signOut() {
        firebaseAuth.signOut()
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