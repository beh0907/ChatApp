package com.skymilk.chatapp.store.domain.usecase

import com.skymilk.chatapp.store.domain.repository.AuthRepository
import javax.inject.Inject

class SignOut @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke() {
        return repository.signOut()
    }

}