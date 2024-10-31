package com.skymilk.chatapp.store.domain.usecase.chat

import com.skymilk.chatapp.store.data.dto.ParticipantStatus
import com.skymilk.chatapp.store.domain.repository.ChatRepository
import com.skymilk.chatapp.store.presentation.screen.main.chatRoom.MessageEvent
import kotlinx.coroutines.flow.Flow

class GetParticipantsStatus(
    private val chatRepository: ChatRepository
) {
    operator fun invoke(chatRoomId: String): Flow<List<ParticipantStatus>> {
        return chatRepository.getParticipantsStatus(chatRoomId)
    }
}