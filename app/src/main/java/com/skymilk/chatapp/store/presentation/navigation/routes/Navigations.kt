package com.skymilk.chatapp.store.presentation.navigation.routes

sealed class Navigations(val route: String) {
    data object Start : Navigations("StartNavigation")
    data object Auth : Navigations("AuthNavigation")
    data object Main : Navigations("MainNavigation")
}