package com.skymilk.chatapp.store.domain.usecase.chat

import com.skymilk.chatapp.store.domain.repository.ChatRepository

class CreateChatRoom(
    private val chatRepository: ChatRepository
) {
    suspend operator fun invoke(name: String, participants: List<String>): Result<String> {
        return chatRepository.createChatRoom(name, participants)
    }
}