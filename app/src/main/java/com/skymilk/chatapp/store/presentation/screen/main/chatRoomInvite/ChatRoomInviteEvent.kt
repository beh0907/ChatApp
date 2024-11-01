package com.skymilk.chatapp.store.presentation.screen.main.chatRoomInvite

import com.skymilk.chatapp.store.data.dto.User

sealed interface ChatRoomInviteEvent {

    data class GetChatRoomId(
        val currentUser: User,
        val participants: List<User>,
        val onNavigateToChatRoom: (String) -> Unit
    ) : ChatRoomInviteEvent

    data class AddParticipants(
        val currentUser: User,
        val chatRoomId: String,
        val participants: List<User>,
        val onNavigateToChatRoom: (String) -> Unit
    ) : ChatRoomInviteEvent

}