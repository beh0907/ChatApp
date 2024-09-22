package com.skymilk.chatapp.store.domain.usecase.chat

import com.skymilk.chatapp.store.data.dto.ChatRoom
import com.skymilk.chatapp.store.domain.model.ChatRoomWithUsers
import com.skymilk.chatapp.store.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetChatRooms @Inject constructor(
    private val chatRepository: ChatRepository
) {
    operator fun invoke(userId: String): Flow<List<ChatRoomWithUsers>> {
        return chatRepository.getChatRooms(userId)
    }

}