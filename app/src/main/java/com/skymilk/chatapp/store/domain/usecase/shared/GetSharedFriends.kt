package com.skymilk.chatapp.store.domain.usecase.shared

import com.skymilk.chatapp.store.data.dto.User
import com.skymilk.chatapp.store.domain.repository.SharedRepository
import kotlinx.coroutines.flow.Flow

class GetSharedFriends(
    private val sharedRepository: SharedRepository
) {
    operator fun invoke() : Flow<List<User>> {
        return sharedRepository.getFriends()
    }
}
