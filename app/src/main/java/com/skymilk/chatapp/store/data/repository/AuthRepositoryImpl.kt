package com.skymilk.chatapp.store.data.repository

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase
import com.skymilk.chatapp.BuildConfig
import com.skymilk.chatapp.store.domain.model.User
import com.skymilk.chatapp.store.domain.model.toUser
import com.skymilk.chatapp.store.domain.repository.AuthRepository
import com.skymilk.chatapp.utils.FirebaseAuthErrorHandler
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseDatabase: FirebaseDatabase,
    @ApplicationContext private val context: Context
) : AuthRepository {

    override suspend fun signInWithGoogle(): Result<User> = withContext(Dispatchers.IO) {
        try {
            val credentialManager: CredentialManager = CredentialManager.create(context)

            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(BuildConfig.GOOGLE_AUTH_WEB_CLIENT_ID)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result = credentialManager.getCredential(
                context = context,
                request = request
            )
            val credential = result.credential
            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
            val googleIdToken = googleIdTokenCredential.idToken
            val firebaseCredential = GoogleAuthProvider.getCredential(googleIdToken, null)

            val authResult = firebaseAuth.signInWithCredential(firebaseCredential).await()
            val user = authResult.user?.toUser()

            //회원 등록과 마지막 로그인 시간이 1초 이내인 경우
            //최초 로그인으로 판단해 유저 정보를 데이터베이스에 저장
            val timeDifference = kotlin.math.abs(authResult.user?.metadata?.lastSignInTimestamp!! - authResult.user?.metadata?.creationTimestamp!!)
            if (timeDifference < 1000) saveUserToDatabase(user = user!!)

            Result.success(user!!)
        } catch (e: FirebaseAuthException) {
            e.printStackTrace()

            // FirebaseAuthErrorHandler로 에러 메시지 처리
            val errorMessage = FirebaseAuthErrorHandler.getErrorMessage(e)

            Result.failure(Exception(errorMessage))
        } catch (e:Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    override suspend fun signInWithEmailAndPassword(
        email: String,
        password: String
    ): Result<User> = withContext(Dispatchers.IO) {
        try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val user = result.user?.toUser()

            Result.success(user!!)
        } catch (e: FirebaseAuthException) {
            e.printStackTrace()

            // FirebaseAuthErrorHandler로 에러 메시지 처리
            val errorMessage = FirebaseAuthErrorHandler.getErrorMessage(e)

            Result.failure(Exception(errorMessage))
        } catch (e:Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    override suspend fun signUpWithEmailAndPassword(
        name:String,
        email: String,
        password: String
    ): Result<User> = withContext(Dispatchers.IO) {
        try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user?.toUser()!!

            //이름 적용
            user.username = name

            //가입한 유저 정보를 데이터베이스에 저장
            saveUserToDatabase(user)

            Result.success(user)
        } catch (e: FirebaseAuthException) {
            e.printStackTrace()

            // FirebaseAuthErrorHandler로 에러 메시지 처리
            val errorMessage = FirebaseAuthErrorHandler.getErrorMessage(e)

            Result.failure(Exception(errorMessage))
        } catch (e:Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    override fun getCurrentUser(): User? {
        return firebaseAuth.currentUser?.toUser()
    }

    override suspend fun signOut() {
        //파이어베이스 로그아웃
        firebaseAuth.signOut()
    }

    override suspend fun saveUserToDatabase(user: User) {
        firebaseDatabase.getReference("users")
            .child(user.id)
            .setValue(user)
            .await()
    }


}