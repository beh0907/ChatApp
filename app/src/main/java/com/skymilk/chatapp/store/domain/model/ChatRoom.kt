package com.skymilk.chatapp.store.domain.model

data class ChatRoom(
    val id: String,
    val name: String,
    val participants: List<String>,
    val lastMessage: String,
    val lastMessageTimestamp: Long
)