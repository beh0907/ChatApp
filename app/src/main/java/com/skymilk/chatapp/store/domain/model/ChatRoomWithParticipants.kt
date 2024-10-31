package com.skymilk.chatapp.store.domain.model

data class ChatRoomWithParticipants(
    val id: String,
    val name: String,
    val participants: List<User>,
    val lastMessage: String,
    val lastMessageTimestamp: Long,
    val createdTimestamp: Long,
    val totalMessagesCount: Long // 채팅 총 메시지 수
)