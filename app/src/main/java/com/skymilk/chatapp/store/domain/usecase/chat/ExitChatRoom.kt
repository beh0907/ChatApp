package com.skymilk.chatapp.store.domain.usecase.chat

import com.skymilk.chatapp.store.domain.repository.ChatRepository

class ExitChatRoom(
    private val chatRepository: ChatRepository
) {
    suspend operator fun invoke(
        chatRoomId: String,
        userId: String,
    ): Result<Unit> {
        return chatRepository.exitChatRoom(chatRoomId, userId)
    }

}