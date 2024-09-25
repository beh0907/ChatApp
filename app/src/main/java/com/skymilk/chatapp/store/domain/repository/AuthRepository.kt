package com.skymilk.chatapp.store.domain.repository

import com.skymilk.chatapp.store.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    fun signInWithGoogle(): Flow<User>

    fun signInWithEmailAndPassword(email: String, password: String): Flow<User>

    fun signUpWithEmailAndPassword(name:String, email: String, password: String): Flow<User>

    fun getCurrentUser(): Flow<User>

    suspend fun signOut()

    suspend fun saveUserToDatabase(user: User)
}