package com.skymilk.chatapp.store.domain.repository

import com.skymilk.chatapp.store.data.dto.ChatRoom
import com.skymilk.chatapp.store.domain.model.ChatMessage
import com.skymilk.chatapp.store.domain.model.ChatRoomWithUsers
import com.skymilk.chatapp.store.domain.model.MessageType
import com.skymilk.chatapp.store.domain.model.User
import kotlinx.coroutines.flow.Flow

interface ChatRepository {

    fun getChatRoom(chatRoomId: String): Flow<ChatRoomWithUsers>

    fun getChatRooms(userId: String): Flow<List<ChatRoomWithUsers>>

    suspend fun getOrCreateChatRoom(participants: List<String>): Result<String>

    suspend fun createChatRoom(name: String, participants: List<String>): Result<String>

    suspend fun addParticipants(chatRoomId: String, newParticipants: List<String>): Result<String>

    fun getMessages(chatRoomId: String): Flow<List<ChatMessage>>

    suspend fun sendMessage(
        chatRoomId: String,
        sender: User,
        content: String,
        participants: List<User>,
        type: MessageType
    ): Result<Unit>

    suspend fun sendImageMessage(
        chatRoomId: String,
        sender: User,
        content: String,
        participants: List<User>
    ): Result<Unit>

    suspend fun exitChatRoom(chatRoomId: String, user: User): Result<Unit>
}