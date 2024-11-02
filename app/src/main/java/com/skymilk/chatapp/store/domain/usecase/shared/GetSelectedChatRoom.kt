package com.skymilk.chatapp.store.domain.usecase.shared

import com.skymilk.chatapp.store.data.dto.User
import com.skymilk.chatapp.store.domain.model.ChatRoomWithParticipants
import com.skymilk.chatapp.store.domain.repository.SharedRepository
import kotlinx.coroutines.flow.Flow

class GetSelectedChatRoom(
    private val sharedRepository: SharedRepository
) {
    operator fun invoke(chatRoomId: String) : Flow<ChatRoomWithParticipants?> {
        return sharedRepository.getSelectedChatRoom(chatRoomId)
    }
}
