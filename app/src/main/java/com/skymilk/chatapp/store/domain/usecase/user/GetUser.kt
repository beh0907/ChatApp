package com.skymilk.chatapp.store.domain.usecase.user

import com.skymilk.chatapp.store.data.dto.User
import com.skymilk.chatapp.store.domain.repository.UserRepository

class GetUser(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(userId: String): Result<User> {
        return userRepository.getUser(userId)
    }

}