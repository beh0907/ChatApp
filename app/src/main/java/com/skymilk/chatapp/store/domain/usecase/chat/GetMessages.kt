package com.skymilk.chatapp.store.domain.usecase.chat

import com.skymilk.chatapp.store.domain.model.ChatMessage
import com.skymilk.chatapp.store.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMessages(
    private val chatRepository: ChatRepository
) {
    operator fun invoke(chatRoomId: String): Flow<List<ChatMessage>> {
        return chatRepository.getMessages(chatRoomId)
    }

}