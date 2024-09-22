package com.skymilk.chatapp.store.presentation.navigation.routes

sealed class AuthNavigation(val route: String) {
    data object SignInScreen : MainNavigation("SignInScreen") // 로그인 화면
    data object SignUpScreen : MainNavigation("SignUpScreen") // 회원가입 화면
}