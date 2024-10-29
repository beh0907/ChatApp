package com.skymilk.chatapp.store.data.repository

import android.content.Context
import android.util.Log
import androidx.compose.ui.util.fastDistinctBy
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.toObject
import com.skymilk.chatapp.store.data.dto.ChatRoom
import com.skymilk.chatapp.store.data.dto.FcmAndroidSetting
import com.skymilk.chatapp.store.data.dto.FcmMessage
import com.skymilk.chatapp.store.data.dto.Message
import com.skymilk.chatapp.store.data.dto.ParticipantStatus
import com.skymilk.chatapp.store.data.remote.FcmApi
import com.skymilk.chatapp.store.data.utils.FirebaseUtil
import com.skymilk.chatapp.store.domain.model.ChatMessage
import com.skymilk.chatapp.store.domain.model.ChatRoomWithParticipants
import com.skymilk.chatapp.store.domain.model.MessageContent
import com.skymilk.chatapp.store.domain.model.MessageType
import com.skymilk.chatapp.store.domain.model.Participant
import com.skymilk.chatapp.store.domain.model.User
import com.skymilk.chatapp.store.domain.repository.ChatRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject
import kotlin.collections.listOf
import kotlin.collections.map
import kotlin.collections.mapOf

class ChatRepositoryImpl @Inject constructor(
    private val firebaseDatabase: FirebaseDatabase,
    private val firebaseFireStore: FirebaseFirestore,
    private val fcmApi: FcmApi,
    @ApplicationContext private val context: Context
) : ChatRepository {

    //채팅방 아이디로 채팅방 정보 불러오기
    override fun getChatRoom(chatRoomId: String): Flow<ChatRoomWithParticipants> = callbackFlow {
        val listener = firebaseFireStore.collection("chatRooms").document(chatRoomId)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    close(e)
                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists()) {
                    val chatRoom = snapshot.toObject<ChatRoom>()!!
                    launch {
                        getUsersForParticipants(chatRoom.participantIds).collect { users ->
                            val participants = users.map { user ->
                                Participant(
                                    user = user,
                                    participantStatus = chatRoom.participantsStatus.find { it.userId == user.id }!!
                                )
                            }

                            val chatRoomWithParticipants = ChatRoomWithParticipants(
                                id = chatRoom.id,
                                name = chatRoom.name,
                                participants = participants,
                                lastMessage = chatRoom.lastMessage,
                                lastMessageTimestamp = chatRoom.lastMessageTimestamp,
                                createdTimestamp = chatRoom.createdTimestamp,
                                totalMessagesCount = chatRoom.totalMessagesCount
                            )

                            trySend(chatRoomWithParticipants)
                        }
                    }
                }
            }
        awaitClose { listener.remove() }
    }

    // 나의 채팅방 목록 실시간으로 가져오기
    override fun getChatRooms(userId: String): Flow<List<ChatRoomWithParticipants>> = callbackFlow {
        val listener = firebaseFireStore.collection("chatRooms")
            .whereArrayContains("participantIds", userId)
            .orderBy("lastMessageTimestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    Log.d("getChatRooms error", error.message.toString())
                    close()
                    return@addSnapshotListener
                }

                // 채팅방 목록을 가져온 후, participants를 기반으로 사용자 정보를 비동기적으로 가져오기
                launch {
                    val chatRooms = snapshots?.documents?.mapNotNull { doc ->
                        doc.toObject(ChatRoom::class.java)
                    }.orEmpty()

                    // participants를 기반으로 User 정보를 가져오기
                    val chatRoomWithParticipantsList = chatRooms.map { chatRoom ->
                        getUsersForParticipants(chatRoom.participantIds).map { users ->
                            //실시간 유저 정보와 유저별 채팅방 내 상태정보 결합
                            val participants = users.map { user ->
                                Participant(
                                    user = user,
                                    participantStatus = chatRoom.participantsStatus.find { it.userId == user.id }!!
                                )
                            }

                            ChatRoomWithParticipants(
                                id = chatRoom.id,
                                name = chatRoom.name,
                                participants = participants,
                                lastMessage = chatRoom.lastMessage,
                                lastMessageTimestamp = chatRoom.lastMessageTimestamp,
                                createdTimestamp = chatRoom.createdTimestamp,
                                totalMessagesCount = chatRoom.totalMessagesCount
                            )
                        }
                    }

                    // 빈 목록일 경우 빈 리스트 반환
                    if (chatRoomWithParticipantsList.isEmpty()) {
                        trySend(emptyList())
                    } else {
                        // 채팅방 정보들 결합 후 리턴
                        combine(chatRoomWithParticipantsList) { it.toList() }.collect {
                            trySend(it)
                        }
                    }
                }
            }

        awaitClose { listener.remove() }
    }

    //채팅방 참가자가 일치한 채팅방 아이디 가져오기
    override suspend fun getOrCreateChatRoom(participants: List<String>): Result<String> =
        withContext(Dispatchers.IO) {
            try {
                // 참가자 목록을 정렬하여 일관성 있는 쿼리를 보장합니다.
                val sortedParticipants = participants.sorted()

                // 기존 채팅방 검색
                val existingChatRoom = firebaseFireStore.collection("chatRooms")
                    .whereEqualTo("participantIds", sortedParticipants)
                    .get()
                    .await()
                    .documents
                    .firstOrNull()

                if (existingChatRoom != null) {
                    // 기존 채팅방이 있으면 해당 ID 반환
                    return@withContext Result.success(existingChatRoom.id)
                } else {
                    // 기존 채팅방이 없으면 새로 생성
                    val newChatRoom = createChatRoom(
                        name = "새로운 채팅방", // 또는 참가자 이름을 조합하여 생성할 수 있습니다.
                        participants = sortedParticipants
                    )

                    return@withContext when {
                        newChatRoom.isSuccess -> {
                            Result.success(newChatRoom.getOrThrow())
                        }

                        else -> Result.failure(Exception("채팅방을 생성할 수 없습니다"))
                    }
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    // 채팅방 생성 (Firestore)
    override suspend fun createChatRoom(
        name: String,
        participants: List<String>
    ): Result<String> = withContext(Dispatchers.IO) {
        val key = firebaseDatabase.reference.push().key ?: UUID.randomUUID().toString()
        val timestamp = System.currentTimeMillis()
        val participantsStatus = participants.map {
            ParticipantStatus(userId = it, joinTimestamp = timestamp, lastReadTimestamp = timestamp)
        }

        val chatRoom = ChatRoom(
            id = key,
            name = name,
            participantIds = participants,
            participantsStatus = participantsStatus,
            lastMessage = "",
            lastMessageTimestamp = timestamp,
            createdTimestamp = timestamp,
            totalMessagesCount = 0
        )
        try {
            firebaseFireStore.collection("chatRooms").document(key).set(chatRoom).await()
            Result.success(chatRoom.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    //기존 채팅방에 대화상대 유저 추가
    override suspend fun addParticipants(
        chatRoomId: String,
        newParticipants: List<String>
    ): Result<String> =
        withContext(Dispatchers.IO) {
            try {
                val query = firebaseFireStore.collection("chatRooms").document(chatRoomId)
                firebaseFireStore.runTransaction { transaction ->
                    //채팅방의 마지막 메시지 카운트 가져오기
                    val lastReadMessageCount =
                        transaction.get(query).getLong("totalMessagesCount") ?: 0

                    //새 참가자의 상태 정보 생성
                    val timestamp = System.currentTimeMillis()
                    val participantsStatus = newParticipants.map {
                        ParticipantStatus(
                            userId = it,
                            joinTimestamp = timestamp,
                            lastReadTimestamp = timestamp,
                            lastReadMessageCount = lastReadMessageCount
                        )
                    }

                    // FieldValue.arrayUnion()을 사용하여 새 참가자를 추가합니다.
                    val chatRoomUpdates = mapOf(
                        "participantIds" to FieldValue.arrayUnion(*newParticipants.toTypedArray()),
                        "participantsStatus" to FieldValue.arrayUnion(*participantsStatus.toTypedArray())
                    )
                    query.update(chatRoomUpdates)
                }.await()

                Result.success(chatRoomId)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override suspend fun updateParticipantsStatus(
        chatRoomId: String,
        userId: String,
        originParticipantStatus: ParticipantStatus,
        updateParticipantStatus: ParticipantStatus
    ) {
        val batch = firebaseFireStore.batch()

        // 기존 participantsStatus에서 originParticipantStatus 제거
        val chatRoomRef = firebaseFireStore.collection("chatRooms").document(chatRoomId)
        batch.update(chatRoomRef, "participantsStatus", FieldValue.arrayRemove(originParticipantStatus))

        // updateParticipantStatus 추가
        batch.update(chatRoomRef, "participantsStatus", FieldValue.arrayUnion(updateParticipantStatus))

        // 변경사항 커밋
        batch.commit().await()
    }

    // 채팅방 채팅 목록 실시간으로 가져오기 (Realtime Database)
    override fun getRealtimeMessages(chatRoomId: String): Flow<List<ChatMessage>> = callbackFlow {
        val query = firebaseDatabase.getReference("messages").child(chatRoomId)
        query.keepSynced(true) // 오프라인 사용 동기화

        val listener = query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val chatMessages =
                    snapshot.children.mapNotNull { it.getValue(ChatMessage::class.java) }.reversed()
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
        sender: User,
        content: String,
        participants: List<Participant>,
        type: MessageType
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val timestamp = System.currentTimeMillis()
            val chatMessage = ChatMessage(
                id = firebaseDatabase.reference.push().key ?: UUID.randomUUID().toString(),
                senderId = sender.id,
                timestamp = timestamp,
                messageContents = listOf(
                    MessageContent(content = content, type = type)
                )
            )

            // 1. Realtime Database에 메시지 저장
            firebaseDatabase.getReference("messages")
                .child(chatRoomId)
                .push()
                .setValue(chatMessage)
                .await()

            // 2. 시스템 메시지가 아닐 경우에만 추가 처리
            if (type != MessageType.SYSTEM) {
                //채팅방 최신 상태 정보 저장
                val chatRoomRef = firebaseFireStore.collection("chatRooms").document(chatRoomId)
                chatRoomRef.update(
                    mapOf(
                        "lastMessage" to content,
                        "lastMessageTimestamp" to timestamp,
                        "totalMessagesCount" to FieldValue.increment(1),
                    )
                ).await()

                // 3. FCM 메시지 전송
                sendFcmMessage(
                    chatRoomId = chatRoomId,
                    sender = sender,
                    body = content,
                    tokens = participants
                        .map { it.user.fcmToken }
                        .filter { it != sender.fcmToken }
                        .distinct()
                )
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 이미지 메시지 전달 (Realtime Database)
    override suspend fun sendImageMessage(
        chatRoomId: String,
        sender: User,
        imageUrls: List<String>,
        participants: List<Participant>
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val timestamp = System.currentTimeMillis()

            val chatMessage = ChatMessage(
                id = firebaseDatabase.reference.push().key ?: UUID.randomUUID().toString(),
                senderId = sender.id,
                timestamp = System.currentTimeMillis(),
                messageContents = imageUrls.map { imageUrl ->
                    MessageContent(
                        content = imageUrl,
                        type = MessageType.IMAGE
                    )
                }
            )

            //채팅방 최신 상태 정보 저장
            val chatRoomRef = firebaseFireStore.collection("chatRooms").document(chatRoomId)
            chatRoomRef.update(
                mapOf(
                    "lastMessage" to "사진 ${imageUrls.size}장을 보냈습니다.",
                    "lastMessageTimestamp" to timestamp,
                    "totalMessagesCount" to FieldValue.increment(1),
                )
            ).await()

            // 메시지를 Realtime Database에 저장
            firebaseDatabase.getReference("messages")
                .child(chatRoomId)
                .push()
                .setValue(chatMessage)
                .await()

            //메시지가 업데이트 되었다면 토픽 그룹으로 알림 전달
            sendFcmMessage(
                chatRoomId = chatRoomId,
                sender = sender,
                body = "사진 ${imageUrls.size}장을 보냈습니다.",

                // 현재 유저를 제외한 참가자들의 FCM 토큰을 가져옵니다.
                // 한 계정에 복수의 로그인 이력이 있을 수 있으니 중복 제거
                tokens = participants.map { it.user.fcmToken }.filter { sender.fcmToken != it }
                    .fastDistinctBy { it })

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 참가자 아이디를 사용하여 User 정보를 가져오는 함수
    fun getUsersForParticipants(participants: List<String>): Flow<List<User>> = callbackFlow {
        if (participants.isNotEmpty()) {
            val usersRef = firebaseFireStore.collection("users")
            val listener = usersRef
                .whereIn(FieldPath.documentId(), participants)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        close(error)
                        return@addSnapshotListener
                    }

                    val users = snapshot?.documents?.mapNotNull { doc ->
                        doc.toObject(User::class.java)
                    } ?: emptyList()

                    trySend(users)
                }

            awaitClose { listener.remove() }
        } else {
            trySend(emptyList())
            close()
        }
    }

    override suspend fun exitChatRoom(
        chatRoomId: String,
        userId: String,
        participantStatus: ParticipantStatus
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            //참여자 정보 제거
            firebaseFireStore.collection("chatRooms").document(chatRoomId)
                .update(
                    mapOf(
                        "participantIds" to FieldValue.arrayRemove(userId),
                        "participantsStatus" to FieldValue.arrayRemove(participantStatus)
                    )
                ).await()

            // 오프라인 사용 동기화 제거
            firebaseDatabase.getReference("messages")
                .child(chatRoomId)
                .keepSynced(false)

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    //FCM 메시지 전송
//    private suspend fun sendFcmMessage(
//        chatRoomId: String,
//        sender: User,
//        body: String,
//        participants: List<User>
//    ) {
//        val fcmMessage = FcmMessage(
//            message = Message(
////                token = tokens,
//                topic = "${Constants.FCM_TOPIC_PREFIX}$chatRoomId",
//                data = mapOf(
//                    "chatRoomId" to chatRoomId,
//                    "senderId" to sender.id,
//
//                    //notification에 담기면 백그라운드일 경우 자동 처리하는 문제가 생김
//                    //data에 담아야 백그라운드에서 onMessageReceived를 통할 수 있다
//                    "title" to sender.username,
//                    "body" to body
//                ),
//                android = FcmAndroidSetting(directBootOk = true)
//            )
//        )
//
//        val token = "Bearer ${FirebaseUtil.getAccessToken(context)}"
//
//        //메시지와 토큰 전달
//        fcmApi.postNotification(token, fcmMessage)
//    }

    private suspend fun sendFcmMessage(
        chatRoomId: String,
        sender: User,
        body: String,
        tokens: List<String>
    ) = coroutineScope {
        // 토큰이 없는 경우 처리
        if (tokens.isEmpty()) {
            Log.w("FCM", "No valid FCM tokens found for participants")
            return@coroutineScope
        }

        val baseMessage = Message(
            data = mapOf(
                "chatRoomId" to chatRoomId,
                "senderId" to sender.id,
                "title" to sender.username,
                "body" to body
            ),
            android = FcmAndroidSetting(directBootOk = true)
        )

        val accessToken = "Bearer ${FirebaseUtil.getAccessToken(context)}"

        // 각 토큰에 대해 비동기적으로 메시지를 전송합니다.
        tokens.map { deviceToken ->
            async {
                val fcmMessage = FcmMessage(
                    message = baseMessage.copy(token = deviceToken)
                )

                //토큰 전달
                fcmApi.postNotification(accessToken, fcmMessage)
            }
        }.awaitAll()
    }
}