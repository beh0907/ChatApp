package com.skymilk.chatapp.store.presentation.screen.navigation

sealed class Screens(
    val route: String
) {
    data object SignInScreen : Screens("SignInScreen")
    data object SignUpScreen : Screens("SignUpScreen")
    data object MainScreen : Screens("HomeScreen")
    data object FriendsScreen : Screens("FriendsScreen")
    data object ChatListScreen : Screens("ChatListScreen")
    data object ChatRoomScreen : Screens("ChatRoomScreen")
    data object ProfileScreen : Screens("ProfileScreen")
}