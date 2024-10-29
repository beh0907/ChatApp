package com.skymilk.chatapp.store.domain.usecase.chat

import com.skymilk.chatapp.store.domain.model.ChatRoomWithParticipants
import com.skymilk.chatapp.store.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow

class GetChatRoom(
    private val chatRepository: ChatRepository
) {
    operator fun invoke(chatRoomId:String): Flow<ChatRoomWithParticipants> {
        return chatRepository.getChatRoom(chatRoomId)
    }

}