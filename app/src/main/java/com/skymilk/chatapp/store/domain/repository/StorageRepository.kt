package com.skymilk.chatapp.store.domain.repository

import android.net.Uri
import com.skymilk.chatapp.store.domain.model.UploadProgress
import kotlinx.coroutines.flow.Flow

interface StorageRepository {

    suspend fun saveProfileImage(userid: String, uri: Uri): Flow<UploadProgress>

    suspend fun saveChatMessageImage(chatRoomId: String, uri: Uri): Flow<UploadProgress>

    fun saveImage(path: String, uri: Uri): Flow<UploadProgress>
}