package com.skymilk.chatapp.store.domain.usecase.chat

data class ChatUseCases(
    val getChatRoom: GetChatRoom,
    val getChatRooms: GetChatRooms,
    val getOrCreateChatRoom: GetOrCreateChatRoom,
    val getMessages: GetMessages,
    val sendMessage: SendMessage,
    val sendImageMessage: SendImageMessage,
    val createChatRoom: CreateChatRoom,
    val exitChatRoom: ExitChatRoom,
    val addParticipants: AddParticipants,
)
