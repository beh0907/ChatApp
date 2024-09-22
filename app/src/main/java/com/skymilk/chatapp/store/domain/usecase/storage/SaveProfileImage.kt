package com.skymilk.chatapp.store.domain.usecase.storage

import android.net.Uri
import com.skymilk.chatapp.store.domain.model.UploadProgress
import com.skymilk.chatapp.store.domain.repository.StorageRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SaveProfileImage @Inject constructor(
    private val storageRepository: StorageRepository
) {
    suspend operator fun invoke(userId: String, uri: Uri): Flow<UploadProgress> {
        return storageRepository.saveProfileImage(userId, uri)
    }

}