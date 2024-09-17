package com.skymilk.chatapp.store.data.repository

import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.database.FirebaseDatabase
import com.skymilk.chatapp.store.domain.model.User
import com.skymilk.chatapp.store.domain.repository.UserRepository
import com.skymilk.chatapp.utils.FirebaseAuthErrorHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val firebaseDatabase: FirebaseDatabase
) : UserRepository {

    override suspend fun updateProfile(user: User): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun getUser(userId: String): Result<User> = withContext(Dispatchers.IO) {
        try {
            val user = firebaseDatabase.getReference("users").child(userId)
                .get()
                .await()
                .getValue(User::class.java)

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

    override fun getFriends(userId: String): Flow<List<User>> {
        TODO("Not yet implemented")
    }
}