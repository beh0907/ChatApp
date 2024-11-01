package com.skymilk.chatapp.store.domain.usecase.user

import com.skymilk.chatapp.store.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow

class GetIsFriend(
    private val userRepository: UserRepository
) {
    operator fun invoke(myUserId:String, otherUserId:String): Flow<Boolean> {
        return userRepository.getIsFriend(myUserId, otherUserId)
    }

}