package com.skymilk.chatapp.store.domain.usecase.auth

import com.skymilk.chatapp.store.data.dto.User
import com.skymilk.chatapp.store.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow

class GetCurrentUser(
    private val authRepository: AuthRepository
) {
    operator fun invoke(): Flow<User> {
        return authRepository.getCurrentUser()
    }

}