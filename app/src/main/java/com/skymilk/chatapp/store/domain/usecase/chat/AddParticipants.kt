package com.skymilk.chatapp.store.domain.usecase.chat

import com.skymilk.chatapp.store.domain.repository.ChatRepository
import javax.inject.Inject

class AddParticipants(
    private val chatRepository: ChatRepository
) {
    suspend operator fun invoke(chatRoomId: String, newParticipants: List<String>): Result<String> {
        return chatRepository.addParticipants(chatRoomId, newParticipants)
    }
}