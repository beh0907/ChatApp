package com.skymilk.chatapp.store.presentation.navigation.routes

sealed class StartNavigation(val route: String) {
    data object SplashScreen : StartNavigation("SplashScreen") // 로딩 화면
}