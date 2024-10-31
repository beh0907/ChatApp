package com.skymilk.chatapp.store.presentation.screen.main.profile

sealed interface ProfileEvent {

    data class GetChatRoomId(val participants: List<String>) : ProfileEvent

    data class GetFriendState(val myUserId: String, val otherUserId: String) : ProfileEvent

    data class SetFriendState(val myUserId: String, val otherUserId: String, val isFriend: Boolean) : ProfileEvent

}