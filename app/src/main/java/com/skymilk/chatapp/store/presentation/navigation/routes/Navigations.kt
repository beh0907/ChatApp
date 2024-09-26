package com.skymilk.chatapp.store.presentation.navigation.routes

import kotlinx.serialization.Serializable

@Serializable
sealed class Navigations {

    @Serializable
    data object Start : Navigations()

    @Serializable
    data object Auth : Navigations()

    @Serializable
    data object Main : Navigations()
}