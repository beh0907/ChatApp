package com.skymilk.chatapp.store.domain.model

data class ChatMessage(
    val id: String,
    val senderId: String,
    val messageContents: List<MessageContent>,
    val timestamp: Long,
) {
    constructor() : this("", "", listOf(), System.currentTimeMillis())
}

data class MessageContent(
    val content: String = "",
    val type: MessageType = MessageType.TEXT
)



enum class MessageType {
    TEXT, IMAGE, VIDEO, SYSTEM
}