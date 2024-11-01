package com.skymilk.chatapp.store.domain.usecase.auth

import com.skymilk.chatapp.store.data.dto.User
import com.skymilk.chatapp.store.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow

class SignUpWithEmailAndPassword(
    private val authRepository: AuthRepository
) {
    operator fun invoke(name: String, email: String, password: String): Flow<User> {
        return authRepository.signUpWithEmailAndPassword(name, email, password)
    }
}