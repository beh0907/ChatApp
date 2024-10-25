package com.skymilk.chatapp.store.data.repository

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.skymilk.chatapp.store.domain.model.User
import com.skymilk.chatapp.store.domain.repository.UserRepository
import com.skymilk.chatapp.store.data.utils.FirebaseUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val firebaseFireStore: FirebaseFirestore,
//    private val firebaseAuth: FirebaseAuth
) : UserRepository {

    override suspend fun updateProfile(
        userId: String,
        name: String,
        statusMessage: String,
        imageUrl: String?
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val userRef = firebaseFireStore.collection("users").document(userId)

            // Firestore 업데이트
            val updates = hashMapOf<String, Any>(
                "username" to name,
                "statusMessage" to statusMessage
            )
            if (imageUrl != null) {
                updates["profileImageUrl"] = imageUrl
            }
            userRef.update(updates).await()

            // Firebase Auth 프로필 업데이트
//            val currentUser = firebaseAuth.currentUser ?: throw Exception("현재 사용자가 없습니다")
//            val profileUpdates = UserProfileChangeRequest.Builder()
//                .setDisplayName(name)
//                .apply {
//                    if (imageUrl.isNotEmpty()) {
//                        photoUri = Uri.parse(imageUrl)
//                    }
//                }
//                .build()
//            currentUser.updateProfile(profileUpdates).await()

            Result.success(Unit)
        } catch (e: Exception) {
            handleError(e)
        }
    }

    override fun updateFcmToken(userId: String, token: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val userRef = firebaseFireStore.collection("users").document(userId)

                val updates = hashMapOf<String, Any>(
                    "fcmToken" to token
                )

                userRef.update(updates).await()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    override suspend fun getUser(userId: String): Result<User> = withContext(Dispatchers.IO) {
        try {
            val document = firebaseFireStore.collection("users").document(userId).get().await()
            val user = document.toObject(User::class.java)

            if (user != null) {
                Result.success(user)
            } else {
                Result.failure(Exception("사용자를 찾을 수 없습니다"))
            }
        } catch (e: Exception) {
            handleError(e)
        }
    }

    //친구 목록 가져오기
    override fun getFriends(userId: String): Flow<List<User>> = callbackFlow {
        val query = firebaseFireStore.collection("friends").document(userId)
        val usersCollection = firebaseFireStore.collection("users")

        var userListener: ListenerRegistration? = null

        val friendListener = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.d("getFriends error", error.message.toString())
                close()
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                val friendIds = snapshot.get("friendList") as? List<String> ?: emptyList()

                if (friendIds.isNotEmpty()) {
                    // 기존 userListener가 있다면 제거
                    userListener?.remove()

                    // 새로운 userListener 설정
                    userListener = usersCollection.whereIn(FieldPath.documentId(), friendIds)
                        .addSnapshotListener { usersSnapshot, usersError ->
                            if (usersError != null) {
                                Log.d("getFriends users error", usersError.message.toString())
                                close()
                                return@addSnapshotListener
                            }

                            val userList = usersSnapshot?.documents?.mapNotNull { doc ->
                                doc.toObject(User::class.java)
                            } ?: emptyList()

                            trySend(userList)
                        }
                } else {
                    trySend(emptyList())
                }
            } else {
                trySend(emptyList())
            }
        }

        awaitClose {
            friendListener.remove()
            userListener?.remove()
        }
    }

    override fun getIsFriend(myUserId: String, otherUserId: String): Flow<Boolean> = callbackFlow {
        val query = FirebaseFirestore.getInstance().collection("friends").document(myUserId)

        val listener = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close()
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {

                val friendList = snapshot.get("friendList") as? List<String> ?: emptyList()
                trySend(otherUserId in friendList)
            } else {
                trySend(false)
            }
        }

        awaitClose { listener.remove() }
    }

    override suspend fun setFriend(
        myUserId: String,
        otherUserId: String,
        isFriend: Boolean
    ) {
        val query = firebaseFireStore.collection("friends").document(myUserId)

        // 문서가 존재하는지 확인
        val documentSnapshot = query.get().await()
        if (!documentSnapshot.exists()) {
            // friendList 필드가 없는 경우 초기화
            query.set(mapOf("friendList" to listOf<String>()))
        }

        if (isFriend) {
            // otherUserId를 친구 목록에 추가
            query.update("friendList", FieldValue.arrayUnion(otherUserId))
        } else {
            // otherUserId를 친구 목록에서 제거
            query.update("friendList", FieldValue.arrayRemove(otherUserId))
        }
    }

    override suspend fun searchUser(query: String): Result<List<User>> =
        withContext(Dispatchers.IO) {
            try {
                val usersRef = firebaseFireStore.collection("users")

                // ID에 대한 쿼리
                val idQuery = usersRef
                    .whereGreaterThanOrEqualTo("id", query)
                    .whereLessThanOrEqualTo("id", query + '\uf8ff')

                // 이름에 대한 쿼리
                val nameQuery = usersRef
                    .whereGreaterThanOrEqualTo("username", query)
                    .whereLessThanOrEqualTo("username", query + '\uf8ff')

                // 두 쿼리 실행
                val idResults = idQuery.get().await()
                val nameResults = nameQuery.get().await()

                // 결과 합치기 및 중복 제거
                val combinedResults = (idResults.documents + nameResults.documents).distinctBy { it.id }

                // User 객체로 변환
                val users = combinedResults.mapNotNull { document ->
                    document.toObject(User::class.java)
                }

                Log.d("searchUser", "users : $users")

                Result.success(users)
            } catch (e: Exception) {
                Result.failure(e)
            }

        }


    //오류 체크
    private fun <T> handleError(e: Exception): Result<T> {
        e.printStackTrace()
        val errorMessage = when (e) {
            is FirebaseAuthException -> FirebaseUtil.getErrorMessage(e)
            else -> e.message ?: "An unknown error occurred"
        }
        return Result.failure(Exception(errorMessage))
    }
}