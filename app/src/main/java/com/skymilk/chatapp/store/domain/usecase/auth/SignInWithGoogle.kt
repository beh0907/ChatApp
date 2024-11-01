package com.skymilk.chatapp.store.domain.usecase.auth

import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.skymilk.chatapp.store.data.dto.User
import com.skymilk.chatapp.store.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow

class SignInWithGoogle(
    private val authRepository: AuthRepository
) {
    operator fun invoke(googleIdTokenCredential: GoogleIdTokenCredential): Flow<User> {
        return authRepository.signInWithGoogle(googleIdTokenCredential.idToken)
    }

}