package com.skymilk.chatapp.store.presentation.navigation

import androidx.navigation.NavDeepLink
import androidx.navigation.navDeepLink

sealed class Routes(
    val route: String,
    val deepLinks:List<NavDeepLink> = emptyList()
) {
    data object SplashScreen : Routes("SplashScreen")
    data object SignInScreen : Routes("SignInScreen")
    data object SignUpScreen : Routes("SignUpScreen")
    data object MainScreen : Routes("HomeScreen")
    data object FriendsScreen : Routes("FriendsScreen")
    data object ChatListScreen : Routes("ChatListScreen")
    data object ChatRoomScreen : Routes("ChatRoomScreen", deepLinks = listOf(
        navDeepLink {
            uriPattern = "chatapp://ChatRoomScreen/{chatRoomId}"
        }
    ))
    data object ProfileScreen : Routes("ProfileScreen")

    data object StartNavigation : Routes("StartNavigation")
    data object AuthNavigation : Routes("AuthNavigation")
    data object MainNavigation : Routes("MainNavigation")
}