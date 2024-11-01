package com.skymilk.chatapp.store.domain.usecase.auth

import com.skymilk.chatapp.store.domain.repository.AuthRepository

class SignOut(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke() {
        return authRepository.signOut()
    }

}