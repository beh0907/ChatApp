package com.skymilk.chatapp.store.presentation.screen.main.friends

sealed interface FriendsEvent {

    data object LoadFriends : FriendsEvent

}