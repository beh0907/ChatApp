package com.skymilk.chatapp.store.presentation.navigation.routes

import kotlinx.serialization.Serializable

@Serializable
sealed class AuthNavigation {
    @Serializable
    data object SignInScreen : MainNavigation() // 로그인 화면

    @Serializable
    data object SignUpScreen : MainNavigation() // 회원가입 화면
}