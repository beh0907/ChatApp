package com.skymilk.chatapp.store.presentation.navigation.routes

import kotlinx.serialization.Serializable

@Serializable
sealed interface AuthScreens {
    @Serializable
    data object SignInScreen : AuthScreens // 로그인 화면

    @Serializable
    data object SignUpScreen : AuthScreens // 회원가입 화면
}