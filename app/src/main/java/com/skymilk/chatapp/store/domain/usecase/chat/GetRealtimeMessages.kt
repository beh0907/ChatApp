package com.skymilk.chatapp.store.domain.usecase.chat

import com.skymilk.chatapp.store.domain.model.ChatMessage
import com.skymilk.chatapp.store.domain.repository.ChatRepository
import com.skymilk.chatapp.store.presentation.screen.main.chatRoom.MessageEvent
import kotlinx.coroutines.flow.Flow

class GetRealtimeMessages(
    private val chatRepository: ChatRepository
) {
    operator fun invoke(chatRoomId: String, joinTimestamp: Long): Flow<MessageEvent> {
        return chatRepository.getRealtimeMessages(chatRoomId, joinTimestamp)
    }
}