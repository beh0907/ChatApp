package com.skymilk.chatapp.store.domain.usecase.user

import com.skymilk.chatapp.store.domain.model.User
import com.skymilk.chatapp.store.domain.repository.AuthRepository
import com.skymilk.chatapp.store.domain.repository.UserRepository
import javax.inject.Inject

class UpdateProfile @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(user: User): Result<Unit> {
        return userRepository.updateProfile(user)
    }

}