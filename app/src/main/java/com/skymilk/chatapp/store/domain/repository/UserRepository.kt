package com.skymilk.chatapp.store.domain.repository

import com.skymilk.chatapp.store.domain.model.User

interface UserRepository {
    suspend fun updateProfile(user: User): Result<Unit>

    suspend fun getUser(userId: String): Result<User>
}