package com.skymilk.chatapp.store.presentation.screen.main.chatRoom

import android.net.Uri
import com.skymilk.chatapp.store.domain.model.MessageType
import com.skymilk.chatapp.store.domain.model.User

sealed interface ChatRoomEvent {

    data object LoadChatRoom : ChatRoomEvent

    data object LoadChatMessages : ChatRoomEvent

    data class SendMessage(
        val sender: User,
        val content: String,
        val participants: List<User>,
        val type: MessageType = MessageType.TEXT
    ) : ChatRoomEvent

    data class SendImageMessage(
        val sender: User,
        val imageUri: Uri,
        val participants: List<User>,
    ) : ChatRoomEvent

    data class ExitChatRoom(val user: User, val onNavigateToBack: () -> Unit) : ChatRoomEvent

    data object ToggleAlarmState : ChatRoomEvent

    data object SubscribeForNotification : ChatRoomEvent

    data object UnsubscribeForNotification : ChatRoomEvent

}