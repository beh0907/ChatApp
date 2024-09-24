package com.skymilk.chatapp.store.domain.repository

import android.net.Uri
import com.skymilk.chatapp.store.domain.model.UploadProgress
import kotlinx.coroutines.flow.Flow

interface StorageRepository {

    fun saveProfileImage(userid: String, byteArray: ByteArray): Flow<UploadProgress>

    fun saveChatMessageImage(chatRoomId: String, uri: Uri): Flow<UploadProgress>

    fun <T> saveImage(path: String, imageData:T): Flow<UploadProgress>
}