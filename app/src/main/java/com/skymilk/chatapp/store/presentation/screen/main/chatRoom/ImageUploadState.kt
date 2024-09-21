package com.skymilk.chatapp.store.presentation.screen.main.chatRoom

import android.net.Uri

sealed class ImageUploadState {
    data object Initial : ImageUploadState() //초기 상태
    data class Loading(val imageUri: Uri) : ImageUploadState() //로딩 상태
    data class Uploading(
        val progress: Int,
        val bytesTransferred: Long,
        val totalBytes: Long,
        val imageUri: Uri
    ) : ImageUploadState() // 이미지 업로드 중
    data class Success(val downloadUrl: String) : ImageUploadState() // 이미지 업로드 완료
    data class Error(val message: String) : ImageUploadState() // 이미지 업로드 실패
}
