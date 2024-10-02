package com.skymilk.chatapp.store.domain.usecase.auth

import android.app.Activity
import com.skymilk.chatapp.store.domain.model.User
import com.skymilk.chatapp.store.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SignInWithKakao @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke(activity: Activity): Flow<User> {
        return authRepository.signInWithKakao(activity)
    }

}