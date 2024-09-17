package com.skymilk.chatapp.store.domain.repository

import com.skymilk.chatapp.store.domain.model.ChatRoom
import com.skymilk.chatapp.store.domain.model.Message
import kotlinx.coroutines.flow.Flow

interface ChatRepository {

    fun getChatRooms(userId: String): Flow<List<ChatRoom>>

    fun getMessages(chatRoomId: String): Flow<List<Message>>

    suspend fun sendMessage(chatRoomId: String, message: Message): Result<Unit>

    suspend fun createChatRoom(name: String, participants: List<String>): Result<ChatRoom>

}