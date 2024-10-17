package com.skymilk.chatapp.store.domain.usecase.chat

import com.skymilk.chatapp.store.domain.model.User
import com.skymilk.chatapp.store.domain.repository.ChatRepository
import javax.inject.Inject

class ExitChatRoom(
    private val chatRepository: ChatRepository
) {
    suspend operator fun invoke(chatRoomId: String, user: User): Result<Unit> {
        return chatRepository.exitChatRoom(chatRoomId, user)
    }

}