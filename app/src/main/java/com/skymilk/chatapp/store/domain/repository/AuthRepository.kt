package com.skymilk.chatapp.store.domain.repository

import com.skymilk.chatapp.store.domain.model.User

interface AuthRepository {

    suspend fun signInWithGoogle(): Result<User>

    suspend fun signInWithEmailAndPassword(email: String, password: String): Result<User>

    suspend fun signUpWithEmailAndPassword(name:String, email: String, password: String): Result<User>

    suspend fun getCurrentUser(): User?

    suspend fun signOut()

    suspend fun saveUserToDatabase(user: User)
}