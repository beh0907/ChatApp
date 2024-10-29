package com.skymilk.chatapp.store.domain.repository

import com.skymilk.chatapp.store.data.dto.ParticipantStatus
import com.skymilk.chatapp.store.domain.model.ChatMessage
import com.skymilk.chatapp.store.domain.model.ChatRoomWithParticipants
import com.skymilk.chatapp.store.domain.model.MessageType
import com.skymilk.chatapp.store.domain.model.Participant
import com.skymilk.chatapp.store.domain.model.User
import kotlinx.coroutines.flow.Flow

interface ChatRepository {

    fun getChatRoom(chatRoomId: String): Flow<ChatRoomWithParticipants>

    fun getChatRooms(userId: String): Flow<List<ChatRoomWithParticipants>>

    suspend fun getOrCreateChatRoom(participants: List<String>): Result<String>

    suspend fun createChatRoom(name: String, participants: List<String>): Result<String>

    suspend fun addParticipants(chatRoomId: String, newParticipants: List<String>): Result<String>

    suspend fun updateParticipantsStatus(
        chatRoomId: String,
        userId: String,
        originParticipantStatus: ParticipantStatus,
        updateParticipantStatus: ParticipantStatus
    )

    fun getRealtimeMessages(chatRoomId: String): Flow<List<ChatMessage>>

    suspend fun sendMessage(
        chatRoomId: String,
        sender: User,
        content: String,
        participants: List<Participant>,
        type: MessageType
    ): Result<Unit>

    suspend fun sendImageMessage(
        chatRoomId: String,
        sender: User,
        imageUrls: List<String>,
        participants: List<Participant>
    ): Result<Unit>

    suspend fun exitChatRoom(chatRoomId: String, userId: String, participantStatus: ParticipantStatus): Result<Unit>
}