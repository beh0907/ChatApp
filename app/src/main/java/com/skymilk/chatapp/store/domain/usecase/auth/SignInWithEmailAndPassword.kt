package com.skymilk.chatapp.store.domain.usecase.auth

import com.skymilk.chatapp.store.domain.model.User
import com.skymilk.chatapp.store.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SignInWithEmailAndPassword(
    private val authRepository: AuthRepository
) {
    operator fun invoke(email: String, password: String): Flow<User> {
        return authRepository.signInWithEmailAndPassword(email, password)
    }

}