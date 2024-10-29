package com.skymilk.chatapp.store.domain.usecase.chat

import com.skymilk.chatapp.store.data.dto.ParticipantStatus
import com.skymilk.chatapp.store.domain.repository.ChatRepository

class UpdateParticipantsStatus(
    private val chatRepository: ChatRepository
) {
    suspend operator fun invoke(
        chatRoomId: String,
        userId: String,
        originParticipantStatus: ParticipantStatus,
        updateParticipantStatus: ParticipantStatus
    ) {
        chatRepository.updateParticipantsStatus(chatRoomId, userId, originParticipantStatus, updateParticipantStatus)
    }
}