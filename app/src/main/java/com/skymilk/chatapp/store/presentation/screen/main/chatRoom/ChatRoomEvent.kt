package com.skymilk.chatapp.store.presentation.screen.main.chatRoom

import android.net.Uri
import com.skymilk.chatapp.store.domain.model.MessageType
import com.skymilk.chatapp.store.domain.model.Participant
import com.skymilk.chatapp.store.domain.model.User

sealed interface ChatRoomEvent {

    data object LoadChatRoom : ChatRoomEvent

    data object LoadChatMessages : ChatRoomEvent

    data class SendMessage(
        val sender: User,
        val content: String,
        val participants: List<Participant>,
        val type: MessageType = MessageType.TEXT
    ) : ChatRoomEvent

    data class SendImageMessage(
        val sender: User,
        val imageUris: List<Uri>,
        val participants: List<Participant>,
    ) : ChatRoomEvent

    data class ExitChatRoom(val user: User, val onNavigateToBack: () -> Unit) : ChatRoomEvent

    data object ToggleAlarmState : ChatRoomEvent

    data object SubscribeForNotification : ChatRoomEvent

    data object UnsubscribeForNotification : ChatRoomEvent

}