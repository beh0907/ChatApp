package com.skymilk.chatapp.store.domain.usecase.chat

import com.skymilk.chatapp.store.domain.model.User
import com.skymilk.chatapp.store.domain.repository.ChatRepository
import javax.inject.Inject

class SendImageMessage @Inject constructor(
    private val chatRepository: ChatRepository
) {
    suspend operator fun invoke(chatRoomId: String, sender: User, content: String): Result<Unit> {
        return chatRepository.sendImageMessage(chatRoomId, sender, content)
    }

}