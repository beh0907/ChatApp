package com.skymilk.chatapp.store.presentation.navigation

sealed class Routes(
    val route: String
) {
    data object SplashScreen : Routes("SplashScreen")
    data object SignInScreen : Routes("SignInScreen")
    data object SignUpScreen : Routes("SignUpScreen")
    data object MainScreen : Routes("HomeScreen")
    data object FriendsScreen : Routes("FriendsScreen")
    data object ChatListScreen : Routes("ChatListScreen")
    data object ChatRoomScreen : Routes("ChatRoomScreen")
    data object ProfileScreen : Routes("ProfileScreen")

    data object StartNavigation : Routes("StartNavigation")
    data object AuthNavigation : Routes("AuthNavigation")
    data object MainNavigation : Routes("MainNavigation")
}