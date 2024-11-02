package com.skymilk.chatapp.store.domain.usecase.shared

import com.skymilk.chatapp.store.data.dto.User
import com.skymilk.chatapp.store.domain.repository.SharedRepository

class SetSharedFriends(
    private val sharedRepository: SharedRepository
) {
    suspend operator fun invoke(friends: List<User>) {
        sharedRepository.setFriends(friends)
    }
}
