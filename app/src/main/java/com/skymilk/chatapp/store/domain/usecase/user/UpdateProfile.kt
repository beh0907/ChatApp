package com.skymilk.chatapp.store.domain.usecase.user

import com.skymilk.chatapp.store.domain.repository.UserRepository
import javax.inject.Inject

class UpdateProfile(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(
        userId: String,
        name: String,
        statusMessage: String,
        imageUrl: String
    ): Result<Unit> {
        return userRepository.updateProfile(
            userId,
            name,
            statusMessage,
            imageUrl
        )
    }
}