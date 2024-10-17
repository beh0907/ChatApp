package com.skymilk.chatapp.store.domain.usecase.chat

import com.skymilk.chatapp.store.domain.repository.ChatRepository
import javax.inject.Inject

class GetOrCreateChatRoom(
    private val chatRepository: ChatRepository
) {
    suspend operator fun invoke(participants: List<String>): Result<String> {
        return chatRepository.getOrCreateChatRoom(participants)
    }

}