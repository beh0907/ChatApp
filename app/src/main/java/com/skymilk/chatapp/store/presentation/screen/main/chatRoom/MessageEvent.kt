package com.skymilk.chatapp.store.presentation.screen.main.chatRoom

import com.skymilk.chatapp.store.data.dto.ChatMessage

sealed interface MessageEvent {
    data class Initial(val messages: List<ChatMessage>) : MessageEvent
    data class Added(val message: ChatMessage) : MessageEvent
    data class Modified(val message: ChatMessage) : MessageEvent
    data class Removed(val messageId: String) : MessageEvent
}