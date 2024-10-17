package com.skymilk.chatapp.store.presentation.screen.main.profileEdit

import androidx.compose.ui.graphics.ImageBitmap

sealed interface ProfileEditEvent {

    data class UpdateUserProfile(
        val userId: String,
        val name: String,
        val statusMessage: String,
        val imageBitmap: ImageBitmap?
    ) : ProfileEditEvent

}