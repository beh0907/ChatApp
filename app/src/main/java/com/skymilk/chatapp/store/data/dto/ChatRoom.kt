package com.skymilk.chatapp.store.data.dto

import com.google.firebase.Timestamp

//채팅방 DB
data class ChatRoom(
    val id: String, // 채팅방ID
    val name: String, // 채팅방 이름
    val participantIds: List<String>, // 참여자 ID 목록 (새로 추가)
    val lastMessage: String, // 마지막 메시지
    val lastMessageTimestamp: Timestamp, // 마지막 메시지 시간
    val createdTimestamp: Timestamp, // 생성 시간
    val totalMessagesCount: Long // 채팅 총 메시지 수
) {
    constructor() : this(
        "",
        "",
        listOf(),
        "",
        Timestamp.now(),
        Timestamp.now(),
        0
    )
}