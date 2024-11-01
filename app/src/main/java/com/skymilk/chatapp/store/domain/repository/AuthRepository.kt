package com.skymilk.chatapp.store.domain.repository

import com.skymilk.chatapp.store.data.dto.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    fun signInWithGoogle(idToken : String): Flow<User>

    fun signInWithKakao(idToken : String, accessToken : String): Flow<User>

    fun signInWithEmailAndPassword(email: String, password: String): Flow<User>

    fun signUpWithEmailAndPassword(name: String, email: String, password: String): Flow<User>

    fun getCurrentUser(): Flow<User>

    suspend fun signOut()
}