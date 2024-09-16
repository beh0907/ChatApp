package com.skymilk.chatapp.store.domain.usecase.auth

import com.google.firebase.auth.FirebaseUser
import com.skymilk.chatapp.store.domain.repository.AuthRepository
import javax.inject.Inject

class SignInWithEmailAndPassword @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email:String, password:String): Result<FirebaseUser> {
        return authRepository.signInWithEmailAndPassword(email, password)
    }

}