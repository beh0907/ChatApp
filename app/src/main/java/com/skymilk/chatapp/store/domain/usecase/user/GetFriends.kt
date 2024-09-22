package com.skymilk.chatapp.store.domain.usecase.user

import com.skymilk.chatapp.store.domain.model.User
import com.skymilk.chatapp.store.domain.repository.AuthRepository
import com.skymilk.chatapp.store.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFriends @Inject constructor(
    private val userRepository: UserRepository
) {
    operator fun invoke(userId: String): Flow<List<User>> {
        return userRepository.getFriends(userId)
    }

}