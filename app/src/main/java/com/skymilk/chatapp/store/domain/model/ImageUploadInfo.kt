package com.skymilk.chatapp.store.domain.model

import android.net.Uri

data class ImageUploadInfo(
    val imageUri:Uri? = null,
    val bytesTransferred: Long = 0,
    val totalBytes: Long = 0,
    val progress: Int = 0,
    val downloadUrl: String? = null,
    val error: String? = null
)