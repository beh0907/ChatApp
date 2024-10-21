package com.skymilk.chatapp.store.domain.repository

import android.net.Uri
import com.skymilk.chatapp.store.domain.model.ImageUploadInfo
import kotlinx.coroutines.flow.Flow

interface StorageRepository {

    fun saveProfileImage(userid: String, byteArray: ByteArray): Flow<ImageUploadInfo>

    fun saveChatMessageImage(chatRoomId: String, uris: List<Uri>): Flow<List<ImageUploadInfo>>
}