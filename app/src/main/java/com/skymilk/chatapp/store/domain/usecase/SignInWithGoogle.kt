package com.skymilk.chatapp.store.domain.usecase

import com.google.firebase.auth.FirebaseUser
import com.skymilk.chatapp.store.domain.repository.AuthRepository
import javax.inject.Inject

class SignInWithGoogle @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(): Result<FirebaseUser> {
        return repository.signInWithGoogle()
    }

}