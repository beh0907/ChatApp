package com.skymilk.chatapp.store.domain.usecase.chat

import com.skymilk.chatapp.store.domain.repository.ChatRepository
import javax.inject.Inject

class SendImageMessage @Inject constructor(
    private val chatRepository: ChatRepository
) {
    suspend operator fun invoke(chatRoomId: String, senderId: String, content: String): Result<Unit> {
        return chatRepository.sendImageMessage(chatRoomId, senderId, content)
    }

}