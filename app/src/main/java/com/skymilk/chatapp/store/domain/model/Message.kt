package com.skymilk.chatapp.store.domain.model

data class Message(
    val id: String,
    val senderId: String,
    val content: String,
    val timestamp: Long,
    val type: MessageType
)

enum class MessageType {
    TEXT, IMAGE
}