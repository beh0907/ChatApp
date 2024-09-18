package com.skymilk.chatapp.store.data.dto

data class ChatRoom(
    val id: String,
    val name: String,
    val participants: List<String>,
    val lastMessage: String,
    val lastMessageTimestamp: Long,
    val createdTimestamp: Long
){
    constructor() : this("", "", listOf(), "", System.currentTimeMillis(), System.currentTimeMillis())
}