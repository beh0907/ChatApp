package com.skymilk.chatapp.store.domain.usecase.chat

import com.skymilk.chatapp.store.domain.model.Message
import com.skymilk.chatapp.store.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMessages @Inject constructor(
    private val chatRepository: ChatRepository
) {
    operator fun invoke(chatRoomId: String): Flow<List<Message>> {
        return chatRepository.getMessages(chatRoomId)
    }

}