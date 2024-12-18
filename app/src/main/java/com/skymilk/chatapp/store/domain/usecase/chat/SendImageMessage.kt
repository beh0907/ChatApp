package com.skymilk.chatapp.store.domain.usecase.chat

import com.skymilk.chatapp.store.data.dto.ParticipantStatus
import com.skymilk.chatapp.store.data.dto.User
import com.skymilk.chatapp.store.domain.repository.ChatRepository

class SendImageMessage(
    private val chatRepository: ChatRepository
) {
    suspend operator fun invoke(
        chatRoomId: String,
        sender: User,
        imageUrls: List<String>,
        participants: List<User>,
        participantStatus: ParticipantStatus
    ): Result<Unit> {
        return chatRepository.sendImageMessage(
            chatRoomId,
            sender,
            imageUrls,
            participants,
            participantStatus
        )
    }

}