package com.skymilk.chatapp.store.domain.usecase.chat

import com.skymilk.chatapp.store.domain.model.ChatRoomWithUsers
import com.skymilk.chatapp.store.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetChatRoom(
    private val chatRepository: ChatRepository
) {
    operator fun invoke(chatRoomId:String): Flow<ChatRoomWithUsers> {
        return chatRepository.getChatRoom(chatRoomId)
    }

}