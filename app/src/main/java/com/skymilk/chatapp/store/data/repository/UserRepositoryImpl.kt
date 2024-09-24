package com.skymilk.chatapp.store.data.repository

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.skymilk.chatapp.store.domain.model.User
import com.skymilk.chatapp.store.domain.repository.UserRepository
import com.skymilk.chatapp.utils.FirebaseUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val firebaseFireStore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) : UserRepository {

    override suspend fun updateProfile(
        userId: String,
        name: String,
        statusMessage: String,
        imageUrl: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val currentUser = firebaseAuth.currentUser ?: throw Exception("현재 사용자가 없습니다")
            val userRef = firebaseFireStore.collection("users").document(userId)

            // Firestore 업데이트
            val updates = hashMapOf<String, Any>(
                "username" to name,
                "statusMessage" to statusMessage
            )
            if (imageUrl.isNotEmpty()) {
                updates["profileImageUrl"] = imageUrl
            }
            userRef.update(updates).await()

            // Firebase Auth 프로필 업데이트
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .apply {
                    if (imageUrl.isNotEmpty()) {
                        photoUri = Uri.parse(imageUrl)
                    }
                }
                .build()
            currentUser.updateProfile(profileUpdates).await()

            Result.success(Unit)
        } catch (e: Exception) {
            handleUserError(e)
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
            handleUserError(e)
        }
    }

    //친구 목록 가져오기
    override fun getFriends(userId: String): Flow<List<User>> = callbackFlow {
        val query = firebaseFireStore.collection("friends").document(userId)

        val listener = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                val friendIds = snapshot.data?.keys?.toList() ?: emptyList()

                // 친구 정보 가져오기
                firebaseFireStore.collection("users").get().addOnSuccessListener { usersSnapshot ->
                    val friends = friendIds.mapNotNull { friendId ->
                        usersSnapshot.documents.find { it.id == friendId }
                            ?.toObject(User::class.java)
                    }
                    trySend(friends)
                }.addOnFailureListener { exception ->
                    close(exception)
                }
            } else {
                trySend(emptyList())
            }
        }

        awaitClose {
            listener.remove()
        }
    }


    //오류 체크
    private fun <T> handleUserError(e: Exception): Result<T> {
        e.printStackTrace()
        val errorMessage = when (e) {
            is FirebaseAuthException -> FirebaseUtil.getErrorMessage(e)
            else -> e.message ?: "An unknown error occurred"
        }
        return Result.failure(Exception(errorMessage))
    }
}