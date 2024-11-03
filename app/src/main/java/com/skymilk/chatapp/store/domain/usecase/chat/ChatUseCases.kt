package com.skymilk.chatapp.store.domain.usecase.chat

data class ChatUseCases(
    val getChatRoom: GetChatRoom,
    val getChatRooms: GetChatRooms,
    val getOrCreateChatRoom: GetOrCreateChatRoom,
    val getChatMessages: GetChatMessages,
    val sendMessage: SendMessage,
    val sendImageMessage: SendImageMessage,
    val compressImagesUseCase: CompressImagesUseCase,
    val createChatRoom: CreateChatRoom,
    val exitChatRoom: ExitChatRoom,
    val addParticipants: AddParticipants,
    val getParticipantsStatus: GetParticipantsStatus,
    val updateParticipantsStatus: UpdateParticipantsStatus,
)
