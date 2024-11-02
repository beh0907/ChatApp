package com.skymilk.chatapp.store.domain.usecase.shared

import com.skymilk.chatapp.store.domain.model.ChatRoomWithParticipants
import com.skymilk.chatapp.store.domain.repository.SharedRepository

class SetSharedChatRooms(
    private val sharedRepository: SharedRepository
) {
    suspend operator fun invoke(chatRooms: List<ChatRoomWithParticipants>) {
        return sharedRepository.setChatRooms(chatRooms)
    }
}
