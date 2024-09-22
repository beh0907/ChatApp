package com.skymilk.chatapp.store.domain.usecase.chat

import com.skymilk.chatapp.store.data.dto.ChatRoom
import com.skymilk.chatapp.store.domain.repository.ChatRepository
import javax.inject.Inject

class CreateChatRoom @Inject constructor(
    private val chatRepository: ChatRepository
) {
    suspend operator fun invoke(name: String, participants: List<String>): Result<ChatRoom> {
        return chatRepository.createChatRoom(name, participants)
    }
}