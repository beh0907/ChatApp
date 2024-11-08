package com.skymilk.chatapp.store.domain.usecase.chat

import com.skymilk.chatapp.store.data.dto.ParticipantStatus
import com.skymilk.chatapp.store.data.dto.MessageType
import com.skymilk.chatapp.store.data.dto.User
import com.skymilk.chatapp.store.domain.repository.ChatRepository

class SendMessage(
    private val chatRepository: ChatRepository
) {
    suspend operator fun invoke(
        chatRoomId: String,
        sender: User,
        content: String,
        participants: List<User>,
        type: MessageType,
        participantStatus: ParticipantStatus
    ): Result<Unit> {
        return chatRepository.sendMessage(
            chatRoomId,
            sender,
            content,
            participants,
            type,
            participantStatus
        )
    }

}