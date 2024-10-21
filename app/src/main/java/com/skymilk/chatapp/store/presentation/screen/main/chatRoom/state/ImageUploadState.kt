package com.skymilk.chatapp.store.presentation.screen.main.chatRoom.state

import com.skymilk.chatapp.store.domain.model.ImageUploadInfo

sealed interface ImageUploadState {

    data object Initial : ImageUploadState //초기 상태

    data class Progress(
        val imageUploadInfoList: List<ImageUploadInfo>,
        val completedOrFailedImages: Int
    ) :
        ImageUploadState // 이미지 업로드 중

    data class Completed(
        val successfulUploads: List<ImageUploadInfo>, // 업로드 성공 이미지
        val failedUploads: List<ImageUploadInfo> // 업로드 실패 이미지
    ) : ImageUploadState // 이미지 업로드 처리 완료

    data class Error(val message: String) : ImageUploadState // 이미지 업로드 실패
}