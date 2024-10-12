package com.skymilk.chatapp.store.domain.usecase.chat

import com.skymilk.chatapp.store.domain.model.MessageType
import com.skymilk.chatapp.store.domain.model.User
import com.skymilk.chatapp.store.domain.repository.ChatRepository
import javax.inject.Inject

class SendMessage @Inject constructor(
    private val chatRepository: ChatRepository
) {
    suspend operator fun invoke(
        chatRoomId: String,
        sender: User,
        content: String,
        participants: List<User>,
        type: MessageType
    ): Result<Unit> {
        return chatRepository.sendMessage(
            chatRoomId,
            sender,
            content,
            participants,
            type
        )
    }

}