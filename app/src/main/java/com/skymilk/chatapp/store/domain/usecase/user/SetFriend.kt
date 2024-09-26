package com.skymilk.chatapp.store.domain.usecase.user

import com.skymilk.chatapp.store.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SetFriend @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(
        myUserId: String,
        otherUserId: String,
        isFriend: Boolean
    ) {
        return userRepository.setFriend(myUserId, otherUserId, isFriend)
    }

}