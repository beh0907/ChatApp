package com.skymilk.chatapp.store.domain.usecase.auth

import com.skymilk.chatapp.store.domain.repository.AuthRepository
import javax.inject.Inject

class SignOut(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke() {
        return authRepository.signOut()
    }

}