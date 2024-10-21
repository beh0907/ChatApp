package com.skymilk.chatapp.store.domain.usecase.chat

import com.skymilk.chatapp.store.domain.model.User
import com.skymilk.chatapp.store.domain.repository.ChatRepository
import javax.inject.Inject

class SendImageMessage(
    private val chatRepository: ChatRepository
) {
    suspend operator fun invoke(
        chatRoomId: String,
        sender: User,
        imageUrls: List<String>,
        participants: List<User>
    ): Result<Unit> {
        return chatRepository.sendImageMessage(
            chatRoomId,
            sender,
            imageUrls,
            participants
        )
    }

}