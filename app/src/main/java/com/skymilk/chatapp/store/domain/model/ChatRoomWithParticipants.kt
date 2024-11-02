package com.skymilk.chatapp.store.domain.model

import com.skymilk.chatapp.store.data.dto.User

data class ChatRoomWithParticipants(
    val id: String = "",
    val name: String = "",
    val participants: List<User> = emptyList(),
    val lastMessage: String = "",
    val lastMessageTimestamp: Long = 0,
    val createdTimestamp: Long = 0,
    val totalMessagesCount: Long = 0// 채팅 총 메시지 수
)