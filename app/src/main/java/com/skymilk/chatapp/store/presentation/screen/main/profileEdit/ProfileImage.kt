package com.skymilk.chatapp.store.presentation.screen.main.profileEdit

import androidx.compose.ui.graphics.ImageBitmap

sealed class ProfileImage {

    object Initial : ProfileImage()  // 초기 상태

    object Default : ProfileImage() // 기본 이미지

    data class Custom(val imageBitmap: ImageBitmap) : ProfileImage() // 선택 이미지
}