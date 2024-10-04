package com.skymilk.chatapp.store.domain.usecase.auth

import com.kakao.sdk.auth.model.OAuthToken
import com.skymilk.chatapp.store.domain.model.User
import com.skymilk.chatapp.store.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SignInWithKakao @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke(kakaoToken: OAuthToken): Flow<User> {
        return authRepository.signInWithKakao(kakaoToken.idToken!!, kakaoToken.accessToken)
    }

}