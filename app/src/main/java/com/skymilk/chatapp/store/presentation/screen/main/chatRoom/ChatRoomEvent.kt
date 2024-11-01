package com.skymilk.chatapp.store.presentation.screen.main.chatRoom

import android.net.Uri
import com.skymilk.chatapp.store.data.dto.MessageType
import com.skymilk.chatapp.store.data.dto.User

sealed interface ChatRoomEvent {
    data object LoadChatMessages : ChatRoomEvent

    data class SendMessage(
        val sender: User,
        val content: String,
        val participants: List<User>,
        val type: MessageType = MessageType.TEXT
    ) : ChatRoomEvent

    data class SendImageMessage(
        val sender: User,
        val imageUris: List<Uri>,
        val participants: List<User>,
    ) : ChatRoomEvent

    data class ExitChatRoom(val user: User, val onNavigateToBack: () -> Unit) : ChatRoomEvent

    data object ToggleAlarmState : ChatRoomEvent

}