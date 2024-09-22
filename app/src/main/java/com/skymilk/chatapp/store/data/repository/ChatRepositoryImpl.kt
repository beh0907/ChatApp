package com.skymilk.chatapp.store.data.repository

import android.content.Context
import androidx.compose.ui.util.fastMap
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.toObject
import com.skymilk.chatapp.store.data.dto.ChatRoom
import com.skymilk.chatapp.store.data.dto.FcmMessage
import com.skymilk.chatapp.store.data.dto.Message
import com.skymilk.chatapp.store.data.dto.Notification
import com.skymilk.chatapp.store.data.remote.FcmApi
import com.skymilk.chatapp.store.domain.model.ChatMessage
import com.skymilk.chatapp.store.domain.model.ChatRoomWithUsers
import com.skymilk.chatapp.store.domain.model.MessageType
import com.skymilk.chatapp.store.domain.model.User
import com.skymilk.chatapp.store.domain.repository.ChatRepository
import com.skymilk.chatapp.utils.Constants
import com.skymilk.chatapp.utils.FirebaseUtil
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val firebaseDatabase: FirebaseDatabase,
    private val firebaseFireStore: FirebaseFirestore,
    private val fcmApi: FcmApi,
    @ApplicationContext private val context: Context
) : ChatRepository {

    //아이디로 채팅방 정보 불러오기
    override fun getChatRoom(chatRoomId: String): Flow<ChatRoomWithUsers> = callbackFlow {
        val listener = firebaseFireStore.collection("chatRooms").document(chatRoomId)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    close(e)
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    val chatRoom = snapshot.toObject<ChatRoom>()!!
                    launch {
                        val chatRoomWithUsers = ChatRoomWithUsers(
                            id = chatRoom.id,
                            name = chatRoom.name,
                            participants = getUsersForParticipants(chatRoom.participants),
                            lastMessage = chatRoom.lastMessage,
                            lastMessageTimestamp = chatRoom.lastMessageTimestamp,
                            createdTimestamp = chatRoom.createdTimestamp
                        )
                        trySend(chatRoomWithUsers)
                    }
                }
            }

        awaitClose { listener.remove() }
    }

    // 나의 채팅방 목록 실시간으로 가져오기
    override fun getChatRooms(userId: String): Flow<List<ChatRoomWithUsers>> = callbackFlow {
        val listener = firebaseFireStore.collection("chatRooms")
            .whereArrayContains("participants", userId)
            .orderBy("lastMessageTimestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    close(e)
                    return@addSnapshotListener
                }

                // 채팅방 목록을 가져온 후, participants를 기반으로 사용자 정보를 비동기적으로 가져오기
                launch {
                    val chatRooms = snapshots?.documents?.mapNotNull { doc ->
                        doc.toObject(ChatRoom::class.java)
                    }.orEmpty()

                    // participants를 기반으로 User 정보를 가져오기
                    val chatRoomWithUsersList = chatRooms.fastMap { chatRoom ->
                        ChatRoomWithUsers(
                            id = chatRoom.id,
                            name = chatRoom.name,
                            participants = getUsersForParticipants(chatRoom.participants),
                            lastMessage = chatRoom.lastMessage,
                            lastMessageTimestamp = chatRoom.lastMessageTimestamp,
                            createdTimestamp = chatRoom.createdTimestamp
                        )
                    }

                    trySend(chatRoomWithUsersList)
                }
            }

        awaitClose { listener.remove() }
    }

    // 채팅방 채팅 목록 실시간으로 가져오기 (Realtime Database)
    override fun getMessages(chatRoomId: String): Flow<List<ChatMessage>> = callbackFlow {
        val query = firebaseDatabase.getReference("messages")
            .child(chatRoomId)

        val listener = query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val chatMessages =
                    snapshot.children.mapNotNull { it.getValue(ChatMessage::class.java) }
                trySend(chatMessages)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        })

        awaitClose { query.removeEventListener(listener) }
    }

    // 메시지 전달 (Realtime Database)
    override suspend fun sendMessage(
        chatRoomId: String,
        senderId: String,
        content: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val chatMessage = ChatMessage(
                id = firebaseDatabase.reference.push().key ?: UUID.randomUUID().toString(),
                senderId = senderId,
                content = content, // 메시지 내용
                timestamp = System.currentTimeMillis(),
                type = MessageType.TEXT
            )

            // 메시지를 Realtime Database에 저장
            firebaseDatabase.getReference("messages")
                .child(chatRoomId)
                .push()
                .setValue(chatMessage)
                .await()

            // Firestore에 있는 chatRooms의 lastMessage와 lastMessageTimestamp 업데이트
            val chatRoomUpdates = mapOf(
                "lastMessage" to content,  // 마지막 메시지 내용
                "lastMessageTimestamp" to System.currentTimeMillis()  // 마지막 메시지 타임스탬프
            )

            // Firestore chatRooms 컬렉션에서 chatRoomId를 가진 문서 업데이트
            firebaseFireStore.collection("chatRooms")
                .document(chatRoomId)
                .update(chatRoomUpdates)
                .await()

            //메시지가 업데이트 되었다면 토픽 그룹으로 알림 전달
            sendFcmMessage(chatRoomId, senderId, senderId, content)

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 이미지 메시지 전달 (Realtime Database)
    override suspend fun sendImageMessage(
        chatRoomId: String,
        senderId: String,
        content: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val chatMessage = ChatMessage(
                id = firebaseDatabase.reference.push().key ?: UUID.randomUUID().toString(),
                senderId = senderId,
                content = content, // 이미지가 저장된 URL
                timestamp = System.currentTimeMillis(),
                type = MessageType.IMAGE
            )

            // 메시지를 Realtime Database에 저장
            firebaseDatabase.getReference("messages")
                .child(chatRoomId)
                .push()
                .setValue(chatMessage)
                .await()

            // Firestore에 있는 chatRooms의 lastMessage와 lastMessageTimestamp 업데이트
            val chatRoomUpdates = mapOf(
                "lastMessage" to "이미지",  // 이미지 파일을 전송했기 때문에 이미지라 고정 설정
                "lastMessageTimestamp" to System.currentTimeMillis()  // 마지막 메시지 타임스탬프
            )

            // Firestore chatRooms 컬렉션에서 chatRoomId를 가진 문서 업데이트
            firebaseFireStore.collection("chatRooms")
                .document(chatRoomId)
                .update(chatRoomUpdates)
                .await()

            //메시지가 업데이트 되었다면 토픽 그룹으로 알림 전달
            sendFcmMessage(chatRoomId, senderId, senderId, "이미지")

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 채팅방 생성 (Firestore)
    override suspend fun createChatRoom(
        name: String,
        participants: List<String>
    ): Result<ChatRoom> = withContext(Dispatchers.IO) {
        val key = firebaseDatabase.reference.push().key ?: UUID.randomUUID().toString()

        val chatRoom = ChatRoom(
            id = key,
            name = name,
            participants = participants,
            lastMessage = "",
            lastMessageTimestamp = System.currentTimeMillis(),
            createdTimestamp = System.currentTimeMillis()
        )
        try {
            firebaseFireStore.collection("chatRooms").document(key).set(chatRoom).await()
            Result.success(chatRoom)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 참가자 아이디를 사용하여 User 정보를 가져오는 함수
    override suspend fun getUsersForParticipants(participants: List<String>): List<User> {
        return if (participants.isNotEmpty()) {
            // 사용자 정보를 한 번에 가져오기
            val usersRef = firebaseFireStore.collection("users")
            val userDocuments = usersRef
                .whereIn(FieldPath.documentId(), participants)
                .get()
                .await()

            userDocuments.documents.mapNotNull { doc ->
                doc.toObject(User::class.java)
            }
        } else {
            emptyList()
        }
    }

    //FCM 메시지 전송
    override suspend fun sendFcmMessage(
        chatRoomId: String,
        senderId: String,
        title: String,
        body: String
    ) {
        val fcmMessage = FcmMessage(
            message = Message(
                topic = "${Constants.FCM_TOPIC_PREFIX}$chatRoomId",
                notification = Notification(
                    title = title,
                    body = body
                ),
                data = mapOf(
                    "chatRoomId" to chatRoomId,
                    "senderId" to senderId
                )
            )
        )
        val token = "Bearer ${FirebaseUtil.getAccessToken(context)}"

        //메시지와 토큰 전달
        fcmApi.postNotification(token, fcmMessage)
    }
}