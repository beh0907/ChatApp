package com.skymilk.chatapp.store.presentation.navigation.routes

import kotlinx.serialization.Serializable

@Serializable
sealed interface StartNavigation {
    @Serializable
    data object SplashScreen : StartNavigation// 로딩 화면
}