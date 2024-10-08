package com.skymilk.chatapp.store.presentation.navigation.routes

import kotlinx.serialization.Serializable

@Serializable
sealed interface AuthNavigation {
    @Serializable
    data object SignInScreen : AuthNavigation // 로그인 화면

    @Serializable
    data object SignUpScreen : AuthNavigation // 회원가입 화면
}