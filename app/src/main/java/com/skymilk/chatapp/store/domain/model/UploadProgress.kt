package com.skymilk.chatapp.store.domain.model

data class UploadProgress(
    val bytesTransferred: Long = 0,
    val totalBytes: Long = 0,
    val progress: Int = 0,
    val downloadUrl: String? = null
)