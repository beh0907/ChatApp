package com.skymilk.chatapp.store.domain.usecase.auth

import com.google.firebase.auth.FirebaseUser
import com.skymilk.chatapp.store.domain.repository.AuthRepository
import javax.inject.Inject

class SignUpWithEmailAndPassword @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email:String, password:String): Result<FirebaseUser> {
        return repository.signUpWithEmailAndPassword(email, password)
    }

}