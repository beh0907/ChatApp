package com.skymilk.chatapp.store.domain.repository

import com.google.firebase.auth.FirebaseUser

interface AuthRepository {

    suspend fun signInWithGoogle(): Result<FirebaseUser>

    suspend fun signInWithEmailAndPassword(email: String, password: String): Result<FirebaseUser>

    suspend fun signUpWithEmailAndPassword(email: String, password: String): Result<FirebaseUser>

    fun getCurrentUser(): FirebaseUser?

    suspend fun signOut()
}