package com.skymilk.chatapp.store.data.repository

import com.skymilk.chatapp.store.domain.model.User
import com.skymilk.chatapp.store.domain.repository.UserRepository
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(

) : UserRepository {
    override suspend fun updateProfile(user: User): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun getUser(userId: String): Result<User> {
        TODO("Not yet implemented")
    }
}