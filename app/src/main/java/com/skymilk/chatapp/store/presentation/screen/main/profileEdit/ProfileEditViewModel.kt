package com.skymilk.chatapp.store.presentation.screen.main.profileEdit

import android.util.Log
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skymilk.chatapp.store.domain.usecase.storage.StorageUseCases
import com.skymilk.chatapp.store.domain.usecase.user.UserUseCases
import com.skymilk.chatapp.utils.Event
import com.skymilk.chatapp.utils.sendEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileEditViewModel @Inject constructor(
    private val userUseCases: UserUseCases,
    private val storageUseCases: StorageUseCases
) : ViewModel() {

    fun updateUserProfile(
        userId: String,
        name: String,
        statusMessage: String,
        imageBitmap: ImageBitmap?
    ) {

        uploadImage(userId, imageBitmap) { imageUrl ->
            viewModelScope.launch {

                val result = userUseCases.updateProfile(userId, name, statusMessage, imageUrl)

                when {
                    result.isSuccess -> {
                        sendEvent(Event.Toast("프로필이 업데이트 되었습니다."))
                    }

                    result.isFailure -> {
                        sendEvent(Event.Toast(result.exceptionOrNull()?.message ?: "프로필 업데이트에 실패했습니다"))
                    }
                }
            }
        }
    }

    //이미지 업로드
    private fun uploadImage(id: String, imageBitmap: ImageBitmap?, onComplete: (String) -> Unit) {
        //이미지가 없다면 공백 반환
        if (imageBitmap == null) {
            onComplete("")
            return
        }

        viewModelScope.launch {
            try {
                storageUseCases.saveProfileImage(id, imageBitmap).collect { progress ->
                    if (progress.downloadUrl != null) {
                        onComplete(progress.downloadUrl)
                    }
                }
            } catch (e: Exception) {
                onComplete("")
            }
        }
    }
}