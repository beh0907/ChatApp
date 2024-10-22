package com.skymilk.chatapp.store.presentation.navigation.routes

import kotlinx.serialization.Serializable

@Serializable
sealed interface StartScreens {
    @Serializable
    data object SplashScreen : StartScreens// 로딩 화면
}