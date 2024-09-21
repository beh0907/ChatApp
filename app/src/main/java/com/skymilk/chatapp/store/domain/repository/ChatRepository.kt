package com.skymilk.chatapp.store.domain.repository

import com.skymilk.chatapp.store.data.dto.ChatRoom
import com.skymilk.chatapp.store.domain.model.ChatRoomWithUsers
import com.skymilk.chatapp.store.domain.model.ChatMessage
import com.skymilk.chatapp.store.domain.model.User
import kotlinx.coroutines.flow.Flow

interface ChatRepository {

    fun getAllChatRooms(): Flow<List<ChatRoomWithUsers>>

    fun getChatRooms(userId: String): Flow<List<ChatRoomWithUsers>>

    fun getMessages(chatRoomId: String): Flow<List<ChatMessage>>

    suspend fun sendMessage(chatRoomId: String, senderId: String, content: String): Result<Unit>

    suspend fun sendImageMessage(
        chatRoomId: String,
        senderId: String,
        content: String
    ): Result<Unit>

    suspend fun createChatRoom(name: String, participants: List<String>): Result<ChatRoom>

    suspend fun getUsersForParticipants(participants: List<String>): List<User>

    suspend fun sendFcmMessage(chatRoomId: String, senderId: String, title: String, body: String)
}