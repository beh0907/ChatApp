package com.skymilk.chatapp.store.data.repository

import android.content.Context
import android.util.Log
import androidx.compose.ui.util.fastDistinctBy
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.skymilk.chatapp.store.data.dto.ChatMessage
import com.skymilk.chatapp.store.data.dto.ChatRoom
import com.skymilk.chatapp.store.data.dto.FcmAndroidSetting
import com.skymilk.chatapp.store.data.dto.FcmMessage
import com.skymilk.chatapp.store.data.dto.Message
import com.skymilk.chatapp.store.data.dto.MessageType
import com.skymilk.chatapp.store.data.dto.ParticipantStatus
import com.skymilk.chatapp.store.data.dto.User
import com.skymilk.chatapp.store.data.remote.FcmApi
import com.skymilk.chatapp.store.data.utils.Constants
import com.skymilk.chatapp.store.data.utils.FirebaseUtil
import com.skymilk.chatapp.store.domain.model.ChatRoomWithParticipants
import com.skymilk.chatapp.store.domain.repository.ChatRepository
import com.skymilk.chatapp.store.presentation.screen.main.chatRoom.MessageEvent
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
        val query =
            firebaseFireStore.collection(Constants.FirebaseReferences.CHATROOM).document(chatRoomId)

        val listener = query.addSnapshotListener { snapshot, e ->
            if (e != null) {
                close()
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.exists()) {
                val chatRoom = snapshot.toObject(ChatRoom::class.java)!!
                launch {
                    getUsersForParticipants(chatRoom.participantIds).collect { users ->
                        // timestamp가 null일 수 있으므로 안전하게 처리
                        val lastMessageTime = chatRoom.lastMessageTimestamp?.toDate()?.time
                            ?: System.currentTimeMillis()
                        val createdTime = chatRoom.createdTimestamp?.toDate()?.time
                            ?: System.currentTimeMillis()

                        val chatRoomWithParticipants = ChatRoomWithParticipants(
                            id = chatRoom.id,
                            name = chatRoom.name,
                            participants = users,
                            lastMessage = chatRoom.lastMessage,
                            lastMessageTimestamp = lastMessageTime,
                            createdTimestamp = createdTime,
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
        val query = firebaseFireStore.collection(Constants.FirebaseReferences.CHATROOM)
            .whereArrayContains("participantIds", userId)
            .orderBy("lastMessageTimestamp", Query.Direction.DESCENDING)


        val listener = query.addSnapshotListener { snapshots, error ->
            if (error != null) {
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
                    // timestamp가 null일 수 있으므로 안전하게 처리
                    val lastMessageTime = chatRoom.lastMessageTimestamp?.toDate()?.time
                        ?: System.currentTimeMillis()
                    val createdTime = chatRoom.createdTimestamp?.toDate()?.time
                        ?: System.currentTimeMillis()


                    getUsersForParticipants(chatRoom.participantIds).map { users ->
                        ChatRoomWithParticipants(
                            id = chatRoom.id,
                            name = chatRoom.name,
                            participants = users,
                            lastMessage = chatRoom.lastMessage,
                            lastMessageTimestamp = lastMessageTime,
                            createdTimestamp = createdTime,
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
                val existingChatRoom =
                    firebaseFireStore.collection(Constants.FirebaseReferences.CHATROOM)
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

    // 채팅방 생성
    override suspend fun createChatRoom(
        name: String,
        participants: List<String>
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val key = firebaseDatabase.reference.push().key ?: UUID.randomUUID().toString()

            // Firestore용 채팅방 데이터
            val chatRoom = hashMapOf(
                "id" to key,
                "name" to name,
                "participantIds" to participants,
                "lastMessage" to "",
                "lastMessageTimestamp" to FieldValue.serverTimestamp(), // 서버 시간 사용
                "createdTimestamp" to FieldValue.serverTimestamp(),     // 서버 시간 사용
                "totalMessagesCount" to 0
            )

            // RTDB용 참가자 상태 데이터
            val updates = hashMapOf<String, Any>()
            participants.forEach { userId ->
                val status = hashMapOf(
                    "userId" to userId,
                    "joinTimestamp" to ServerValue.TIMESTAMP,     // RTDB 서버 시간 사용
                    "lastReadTimestamp" to ServerValue.TIMESTAMP, // RTDB 서버 시간 사용
                    "lastReadMessageCount" to 0
                )
                updates["$key/${Constants.FirebaseReferences.CHAT_STATUS}/$userId"] = status
            }
            //채팅방 생성
            firebaseFireStore.collection(Constants.FirebaseReferences.CHATROOM).document(key).set(chatRoom).await()

            //참가자 상태 저장
            firebaseDatabase.reference
                .updateChildren(updates)
                .await()

            Result.success(key)
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
                val query = firebaseFireStore.collection(Constants.FirebaseReferences.CHATROOM)
                    .document(chatRoomId)
                val snapshot = query.get().await()

                //채팅방의 마지막 메시지 카운트 가져오기
                val lastReadMessageCount = snapshot.getLong("totalMessagesCount") ?: 0

                // RTDB에 참가자 상태 저장
                val updates = hashMapOf<String, Any>()
                newParticipants.forEach { userId ->
                    val status = hashMapOf(
                        "userId" to userId,
                        "joinTimestamp" to ServerValue.TIMESTAMP,     // RTDB 서버 시간
                        "lastReadTimestamp" to ServerValue.TIMESTAMP, // RTDB 서버 시간
                        "lastReadMessageCount" to lastReadMessageCount
                    )
                    updates["$chatRoomId/${Constants.FirebaseReferences.CHAT_STATUS}/$userId"] = status
                }

                // 참가자 ID 추가 (Firestore)
                query.update(
                    "participantIds",
                    FieldValue.arrayUnion(*newParticipants.toTypedArray())
                ).await()

                // RTDB 업데이트
                firebaseDatabase.reference
                    .updateChildren(updates)
                    .await()

                Result.success(chatRoomId)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override fun getParticipantsStatus(chatRoomId: String): Flow<List<ParticipantStatus>> =
        callbackFlow {
            val query = firebaseDatabase.getReference(chatRoomId)
                .child(Constants.FirebaseReferences.CHAT_STATUS)
            query.keepSynced(true)

            val listener = query.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    trySend(
                        snapshot.children.mapNotNull { it.getValue(ParticipantStatus::class.java) }
                    )

                }

                override fun onCancelled(error: DatabaseError) {
                    close()
                }
            })

            awaitClose { query.removeEventListener(listener) }
        }

    override suspend fun updateParticipantsStatus(
        chatRoomId: String,
        userId: String,
        participantStatus: ParticipantStatus
    ) {
        // HashMap으로 변환하여 서버 시간 사용
        val status = hashMapOf(
            "userId" to participantStatus.userId,
            "joinTimestamp" to participantStatus.joinTimestamp,  // 기존 joinTimestamp 유지
            "lastReadTimestamp" to ServerValue.TIMESTAMP,        // 현재 서버 시간으로 업데이트
            "lastReadMessageCount" to participantStatus.lastReadMessageCount
        )

        firebaseDatabase.getReference(chatRoomId)
            .child(Constants.FirebaseReferences.CHAT_STATUS)
            .child(userId)
            .setValue(status)
            .await()
    }

    // 채팅방 채팅 목록 실시간으로 가져오기 (Realtime Database)
    override fun getChatMessages(chatRoomId: String, joinTimestamp: Long): Flow<MessageEvent> =
        callbackFlow {
            val query = firebaseDatabase.getReference(chatRoomId)
                .child(Constants.FirebaseReferences.MESSAGES)
                .orderByChild("timestamp")
                .startAt(joinTimestamp.toDouble())
                .limitToLast(300)  // 최신 300개만 가져옴
            query.keepSynced(true)

            var initialDataLoaded = false
            val processedMessageIds = mutableSetOf<String>() // 초기 데이터 중복 방지용

            val listener = query.addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val messageId = snapshot.key ?: return
                    snapshot.getValue(ChatMessage::class.java)?.let { message ->
                        // 초기 데이터 로드가 완료되고, 새로운 메시지인 경우만 Added 이벤트 발생
                        if (initialDataLoaded && !processedMessageIds.contains(messageId)) {
                            trySend(MessageEvent.Added(message))
                        }
                        processedMessageIds.add(messageId)
                    }
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    snapshot.getValue(ChatMessage::class.java)?.let { message ->
                        trySend(MessageEvent.Modified(message))
                    }
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    snapshot.key?.let { messageId ->
                        trySend(MessageEvent.Removed(messageId))
                        processedMessageIds.remove(messageId)
                    }
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                    // timestamp 정렬 사용으로 처리 불필요
                }

                override fun onCancelled(error: DatabaseError) {
                    close()
                }
            })

            // 초기 데이터 로드
            try {
                val initialDataSnapshot = query.get().await()
                val initialMessages = initialDataSnapshot.children
                    .mapNotNull {
                        it.getValue(ChatMessage::class.java)?.also { message ->
                            processedMessageIds.add(it.key ?: "")
                        }
                    }
                    .reversed()
                trySend(MessageEvent.Initial(initialMessages))
                initialDataLoaded = true
            } catch (e: Exception) {
                close()
                return@callbackFlow
            }

            // Flow가 취소될 때 리스너 제거
            awaitClose {
                query.removeEventListener(listener)
                processedMessageIds.clear()
            }
        }

    // 메시지 전달 (Realtime Database)
    override suspend fun sendMessage(
        chatRoomId: String,
        sender: User,
        content: String,
        participants: List<User>,
        type: MessageType,
        participantStatus: ParticipantStatus
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val messageId = firebaseDatabase.reference.push().key ?: UUID.randomUUID().toString()

            // 메시지 생성 시 서버 시간 사용
            val chatMessage = hashMapOf(
                "id" to messageId,
                "senderId" to sender.id,
                "timestamp" to ServerValue.TIMESTAMP,
                "messageContents" to listOf(
                    mapOf(
                        "content" to content,
                        "type" to type.name
                    )
                )
            )

            // Realtime Database 업데이트
            val updates = hashMapOf<String, Any>(
                "${chatRoomId}/${Constants.FirebaseReferences.MESSAGES}/$messageId" to chatMessage
            )
            if (type != MessageType.SYSTEM) {
                updates["${chatRoomId}/${Constants.FirebaseReferences.CHAT_STATUS}/${sender.id}"] = hashMapOf(
                    "userId" to participantStatus.userId,
                    "joinTimestamp" to participantStatus.joinTimestamp,  // 기존 joinTimestamp 유지
                    "lastReadTimestamp" to ServerValue.TIMESTAMP,        // 현재 서버 시간으로 업데이트
                    "lastReadMessageCount" to participantStatus.lastReadMessageCount
                )
            }

            firebaseDatabase.reference
                .updateChildren(updates)
                .await()

            // 시스템 메시지가 아닐 경우 Firestore 업데이트
            if (type != MessageType.SYSTEM) {
                firebaseFireStore.collection(Constants.FirebaseReferences.CHATROOM)
                    .document(chatRoomId)
                    .update(
                        mapOf(
                            "lastMessage" to content,
                            "lastMessageTimestamp" to FieldValue.serverTimestamp(),
                            "totalMessagesCount" to FieldValue.increment(1)
                        )
                    ).await()

                // FCM 메시지 전송
                sendFcmMessage(
                    chatRoomId = chatRoomId,
                    sender = sender,
                    body = content,
                    tokens = participants
                        .map { it.fcmToken }
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
        participants: List<User>,
        participantStatus: ParticipantStatus
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val messageId = firebaseDatabase.reference.push().key ?: UUID.randomUUID().toString()
            val content = "사진 ${imageUrls.size}장을 보냈습니다."

            // 메시지 생성 시 서버 시간 사용
            val chatMessage = hashMapOf(
                "id" to messageId,
                "senderId" to sender.id,
                "timestamp" to ServerValue.TIMESTAMP,
                "messageContents" to imageUrls.map { imageUrl ->
                    mapOf(
                        "content" to imageUrl,
                        "type" to MessageType.IMAGE.name
                    )
                }
            )

            // 상태 정보에도 서버 시간 사용
            val updatedStatus = hashMapOf(
                "userId" to participantStatus.userId,
                "joinTimestamp" to participantStatus.joinTimestamp,  // 기존 joinTimestamp 유지
                "lastReadTimestamp" to ServerValue.TIMESTAMP,        // 현재 서버 시간으로 업데이트
                "lastReadMessageCount" to participantStatus.lastReadMessageCount
            )

            // Realtime Database 업데이트
            firebaseDatabase.reference
                .updateChildren(
                    mapOf(
                        "${chatRoomId}/${Constants.FirebaseReferences.MESSAGES}/$messageId" to chatMessage,
                        "${chatRoomId}/${Constants.FirebaseReferences.CHAT_STATUS}/${sender.id}" to updatedStatus
                    )
                )
                .await()

            // Firestore 업데이트
            firebaseFireStore.collection(Constants.FirebaseReferences.CHATROOM).document(chatRoomId)
                .update(
                    mapOf(
                        "lastMessage" to content,
                        "lastMessageTimestamp" to FieldValue.serverTimestamp(),
                        "totalMessagesCount" to FieldValue.increment(1)
                    )
                ).await()

            // FCM 메시지 전송
            sendFcmMessage(
                chatRoomId = chatRoomId,
                sender = sender,
                body = content,
                tokens = participants
                    .map { it.fcmToken }
                    .filter { sender.fcmToken != it }
                    .fastDistinctBy { it }
            )

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    // 참가자 아이디를 사용하여 User 정보를 가져오는 함수
    fun getUsersForParticipants(participants: List<String>): Flow<List<User>> = callbackFlow {
        if (participants.isNotEmpty()) {
            val usersRef = firebaseFireStore.collection(Constants.FirebaseReferences.USERS)
            val listener = usersRef
                .whereIn(FieldPath.documentId(), participants)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        close()
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
        userId: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            //오프라인 동기화 제거
            firebaseDatabase.getReference(chatRoomId).child(Constants.FirebaseReferences.MESSAGES)
                .keepSynced(false)
            firebaseDatabase.getReference(chatRoomId).child(Constants.FirebaseReferences.CHAT_STATUS)
                .keepSynced(false)

            //참여자 정보 제거
            firebaseFireStore.collection(Constants.FirebaseReferences.CHATROOM).document(chatRoomId)
                .update(
                    mapOf(
                        "participantIds" to FieldValue.arrayRemove(userId),
                    )
                ).await()

            //참여자 상태 정보 제거
            firebaseDatabase.getReference(chatRoomId).child(Constants.FirebaseReferences.CHAT_STATUS)
                .child(userId)
                .removeValue().await()

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
            android = FcmAndroidSetting(priority = "high")
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