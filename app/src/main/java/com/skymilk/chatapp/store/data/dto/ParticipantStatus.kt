package com.skymilk.chatapp.store.data.dto

data class ParticipantStatus(
    val userId: String = "", // 채팅방에 참여한 유저",
    val joinTimestamp: Long = System.currentTimeMillis(), // 채팅방에 참여한 시간
    var lastReadTimestamp: Long = 0, // 마지막으로 읽은 시간
    var lastReadMessageCount: Long = 0, // 마지막으로 읽은 시점의 메시지 수
)