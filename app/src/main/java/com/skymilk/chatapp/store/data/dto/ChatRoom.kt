package com.skymilk.chatapp.store.data.dto

data class ChatRoom(
    val id: String, // 채팅방ID
    val name: String, // 채팅방 이름
    val participants: List<String>, //채팅방 참여자 userId 목록
    val lastMessage: String, // 마지막 메시지
    val lastMessageTimestamp: Long, // 마지막 메시지 시간
    val createdTimestamp: Long, // 생성 시간
    val totalMessagesCount: Long // 채팅 총 메시지 수
) {
    constructor() : this(
        "",
        "",
        listOf(),
        "",
        System.currentTimeMillis(),
        System.currentTimeMillis(),
        0
    )
}