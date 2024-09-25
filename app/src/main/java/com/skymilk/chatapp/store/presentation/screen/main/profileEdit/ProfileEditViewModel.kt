package com.skymilk.chatapp.store.presentation.screen.main.profileEdit

import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skymilk.chatapp.store.domain.usecase.storage.StorageUseCases
import com.skymilk.chatapp.store.domain.usecase.user.UserUseCases
import com.skymilk.chatapp.utils.Event
import com.skymilk.chatapp.utils.sendEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileEditViewModel @Inject constructor(
    private val userUseCases: UserUseCases,
    private val storageUseCases: StorageUseCases
) : ViewModel() {

    private val _editProfileState = MutableStateFlow<EditProfileState>(EditProfileState.Initial)
    val editProfileState = _editProfileState.asStateFlow()

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

                        _editProfileState.update { EditProfileState.Success }
                    }

                    result.isFailure -> {
                        sendEvent(
                            Event.Toast(
                                result.exceptionOrNull()?.message ?: "프로필 업데이트에 실패했습니다"
                            )
                        )

                        _editProfileState.update { EditProfileState.Error }
                    }
                }

            }
        }
    }

    //이미지 업로드
    private fun uploadImage(id: String, imageBitmap: ImageBitmap?, onComplete: (String) -> Unit) {
        viewModelScope.launch {
            _editProfileState.update { EditProfileState.Loading }

            try {
                //imageBitmap가 에러가 발생한다
                storageUseCases.saveProfileImage(id, imageBitmap!!)
                    .collect { progress ->
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