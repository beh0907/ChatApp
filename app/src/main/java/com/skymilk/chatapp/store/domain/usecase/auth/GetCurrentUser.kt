package com.skymilk.chatapp.store.domain.usecase.auth

import com.skymilk.chatapp.store.domain.model.User
import com.skymilk.chatapp.store.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCurrentUser(
    private val authRepository: AuthRepository
) {
    operator fun invoke(): Flow<User> {
        return authRepository.getCurrentUser()
    }

}