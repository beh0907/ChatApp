package com.skymilk.chatapp.store.domain.usecase.auth

import com.kakao.sdk.auth.model.OAuthToken
import com.skymilk.chatapp.store.data.dto.User
import com.skymilk.chatapp.store.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow

class SignInWithKakao(
    private val authRepository: AuthRepository
) {
    operator fun invoke(kakaoToken: OAuthToken): Flow<User> {
        return authRepository.signInWithKakao(kakaoToken.idToken!!, kakaoToken.accessToken)
    }

}