package com.skymilk.chatapp.store.domain.usecase.auth

import com.skymilk.chatapp.store.domain.model.User
import com.skymilk.chatapp.store.domain.repository.AuthRepository
import javax.inject.Inject

class SignInWithEmailAndPassword @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<User> {
        return authRepository.signInWithEmailAndPassword(email, password)
    }

}