package com.skymilk.chatapp.store.domain.usecase.storage

import android.net.Uri
import com.skymilk.chatapp.store.domain.model.ImageUploadInfo
import com.skymilk.chatapp.store.domain.repository.StorageRepository
import kotlinx.coroutines.flow.Flow

class SaveChatMessageImage(
    private val storageRepository: StorageRepository
) {
    operator fun invoke(chatRoomId: String, uris: List<Uri>): Flow<List<ImageUploadInfo>> {
        return storageRepository.saveChatMessageImage(chatRoomId, uris)
    }

}