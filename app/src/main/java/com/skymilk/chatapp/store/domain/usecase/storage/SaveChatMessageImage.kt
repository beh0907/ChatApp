package com.skymilk.chatapp.store.domain.usecase.storage

import android.net.Uri
import com.skymilk.chatapp.store.domain.model.UploadProgress
import com.skymilk.chatapp.store.domain.repository.StorageRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SaveChatMessageImage @Inject constructor(
    private val storageRepository: StorageRepository
) {
    operator fun invoke(chatRoomId: String, uri: Uri): Flow<UploadProgress> {
        return storageRepository.saveChatMessageImage(chatRoomId, uri)
    }

}