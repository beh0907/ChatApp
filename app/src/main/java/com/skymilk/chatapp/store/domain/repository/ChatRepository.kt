package com.skymilk.chatapp.store.domain.repository

import com.skymilk.chatapp.store.data.dto.ParticipantStatus
import com.skymilk.chatapp.store.domain.model.ChatRoomWithParticipants
import com.skymilk.chatapp.store.domain.model.MessageType
import com.skymilk.chatapp.store.domain.model.User
import com.skymilk.chatapp.store.presentation.screen.main.chatRoom.MessageEvent
import kotlinx.coroutines.flow.Flow

interface ChatRepository {

    fun getChatRoom(chatRoomId: String): Flow<ChatRoomWithParticipants>

    fun getChatRooms(userId: String): Flow<List<ChatRoomWithParticipants>>

    suspend fun getOrCreateChatRoom(participants: List<String>): Result<String>

    suspend fun createChatRoom(name: String, participants: List<String>): Result<String>

    suspend fun addParticipants(chatRoomId: String, newParticipants: List<String>): Result<String>

    fun getParticipantsStatus(chatRoomId: String): Flow<List<ParticipantStatus>>

    suspend fun updateParticipantsStatus(
        chatRoomId: String,
        userId: String,
        participantStatus: ParticipantStatus
    )

    fun getChatMessages(chatRoomId: String, joinTimestamp: Long): Flow<MessageEvent>

    suspend fun sendMessage(
        chatRoomId: String,
        sender: User,
        content: String,
        participants: List<User>,
        type: MessageType,
        participantStatus: ParticipantStatus
    ): Result<Unit>

    suspend fun sendImageMessage(
        chatRoomId: String,
        sender: User,
        imageUrls: List<String>,
        participants: List<User>,
        participantStatus: ParticipantStatus
    ): Result<Unit>

    suspend fun exitChatRoom(chatRoomId: String, userId: String): Result<Unit>
}