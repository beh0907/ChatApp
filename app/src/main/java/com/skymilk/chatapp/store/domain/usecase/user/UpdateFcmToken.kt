package com.skymilk.chatapp.store.domain.usecase.user

import com.skymilk.chatapp.store.domain.repository.UserRepository
import javax.inject.Inject

class UpdateFcmToken @Inject constructor(
    private val userRepository: UserRepository
) {
    operator fun invoke(userId: String, token: String) {
        userRepository.updateFcmToken(userId, token)
    }
}