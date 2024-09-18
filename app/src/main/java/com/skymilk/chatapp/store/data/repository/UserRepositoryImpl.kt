package com.skymilk.chatapp.store.data.repository

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.skymilk.chatapp.store.domain.model.User
import com.skymilk.chatapp.store.domain.repository.UserRepository
import com.skymilk.chatapp.utils.FirebaseAuthErrorHandler
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

    override suspend fun updateProfile(user: User): Result<Unit> {
        return try {
            // Firestore에서 사용자 정보 업데이트
            firebaseFireStore.collection("users")
                .document(user.id)
                .set(user)
                .await()

            // Firebase Auth에서 프로필 업데이트
            val currentUser = firebaseAuth.currentUser ?: throw Exception("No current user")

            // photoUrl이 null이 아닐 경우 Uri로 변환
            val photoUri = user.profileImageUrl?.let { Uri.parse(it) }

            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(user.username)
                .setPhotoUri(photoUri)
                .build()

            currentUser.updateProfile(profileUpdates).await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception("프로필 업데이트 중 오류가 발생했습니다"))
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
        } catch (e: FirebaseAuthException) {
            e.printStackTrace()

            val errorMessage = FirebaseAuthErrorHandler.getErrorMessage(e)

            Result.failure(Exception(errorMessage))
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
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
}