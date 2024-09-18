package com.skymilk.chatapp.store.domain.usecase.chat

import com.skymilk.chatapp.store.domain.repository.ChatRepository
import javax.inject.Inject

class SendMessage @Inject constructor(
    private val chatRepository: ChatRepository
) {
    suspend operator fun invoke(chatRoomId: String, senderId: String, content: String): Result<Unit> {
        return chatRepository.sendMessage(chatRoomId, senderId, content)
    }

}