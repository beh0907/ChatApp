package com.skymilk.chatapp.store.domain.usecase.storage

import android.graphics.Bitmap
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import com.skymilk.chatapp.store.domain.model.ImageUploadInfo
import com.skymilk.chatapp.store.domain.repository.StorageRepository
import kotlinx.coroutines.flow.Flow
import java.io.ByteArrayOutputStream

class SaveProfileImage(
    private val storageRepository: StorageRepository
) {
    operator fun invoke(userId: String, bitmap: ImageBitmap): Flow<ImageUploadInfo> {
        return storageRepository.saveProfileImage(userId, convertBitmapToByteArray(bitmap))
    }

    private fun convertBitmapToByteArray(bitmap: ImageBitmap): ByteArray {
        val outputStream = ByteArrayOutputStream()
        bitmap.asAndroidBitmap().compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        return outputStream.toByteArray()
    }

}