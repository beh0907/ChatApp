package com.skymilk.chatapp.store.data.repository

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.skymilk.chatapp.store.domain.model.ChatRoom
import com.skymilk.chatapp.store.domain.model.Message
import com.skymilk.chatapp.store.domain.repository.ChatRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val firebaseDatabase: FirebaseDatabase
) : ChatRepository {

    override fun getChatRooms(): Flow<List<ChatRoom>> = callbackFlow {
        val listener = firebaseDatabase.getReference("chatRooms").addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val chatRooms = snapshot.children.mapNotNull { it.getValue(ChatRoom::class.java) }
                trySend(chatRooms)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        })

        awaitClose { firebaseDatabase.getReference("chatRooms").removeEventListener(listener) }
    }

    override fun getMessages(chatRoomId: String): Flow<List<Message>> = callbackFlow {
        val listener = firebaseDatabase.getReference("messages/$chatRoomId")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val messages = snapshot.children.mapNotNull { it.getValue(Message::class.java) }
                    trySend(messages)
                }

                override fun onCancelled(error: DatabaseError) {
                    close(error.toException())
                }
            })

        awaitClose {
            firebaseDatabase.getReference("messages/$chatRoomId").removeEventListener(listener)
        }
    }

    override suspend fun sendMessage(chatRoomId: String, message: Message): Result<Unit> =
        withContext(
            Dispatchers.IO
        ) {
            try {
                firebaseDatabase.getReference("messages/$chatRoomId").push().setValue(message).await()
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override suspend fun createChatRoom(
        name: String,
        participants: List<String>
    ): Result<ChatRoom> = withContext(
        Dispatchers.IO
    ) {
        val chatRoom = ChatRoom(
            id = UUID.randomUUID().toString(),
            name = name,
            participants = participants,
            lastMessage = "",
            lastMessageTimestamp = System.currentTimeMillis()
        )
        try {
            firebaseDatabase.getReference("chatRooms").push().setValue(chatRoom).await()
            Result.success(chatRoom)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}