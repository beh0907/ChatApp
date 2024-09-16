package com.skymilk.chatapp.store.domain.usecase.chat

data class ChatUseCases(
    val getChatRooms: GetChatRooms,
    val getMessages: GetMessages,
    val sendMessage: SendMessage,
    val sendImageMessage: SendImageMessage,
    val createChatRoom: CreateChatRoom
)
