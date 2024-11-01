package com.skymilk.chatapp.store.domain.usecase.user

import com.skymilk.chatapp.store.data.dto.User
import com.skymilk.chatapp.store.domain.repository.UserRepository

class SearchUser(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(query: String): Result<List<User>> {
        return userRepository.searchUser(query)
    }

}