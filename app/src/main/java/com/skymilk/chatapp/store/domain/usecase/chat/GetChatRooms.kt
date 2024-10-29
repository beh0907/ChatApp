package com.skymilk.chatapp.store.domain.usecase.chat

import com.skymilk.chatapp.store.domain.model.ChatRoomWithParticipants
import com.skymilk.chatapp.store.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow

class GetChatRooms(
    private val chatRepository: ChatRepository
) {
    operator fun invoke(userId: String): Flow<List<ChatRoomWithParticipants>> {
        return chatRepository.getChatRooms(userId)
    }

}