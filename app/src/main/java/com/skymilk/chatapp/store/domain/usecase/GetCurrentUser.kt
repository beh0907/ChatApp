package com.skymilk.chatapp.store.domain.usecase

import com.google.firebase.auth.FirebaseUser
import com.skymilk.chatapp.store.domain.repository.AuthRepository
import javax.inject.Inject

class GetCurrentUser @Inject constructor(
    private val repository: AuthRepository
) {
    operator fun invoke(): FirebaseUser? {
        return repository.getCurrentUser()
    }

}