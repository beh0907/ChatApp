package com.skymilk.chatapp.store.domain.usecase.user

import com.skymilk.chatapp.store.data.dto.User
import com.skymilk.chatapp.store.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow

class GetFriends(
    private val userRepository: UserRepository
) {
    operator fun invoke(userId: String): Flow<List<User>> {
        return userRepository.getFriends(userId)
    }

}