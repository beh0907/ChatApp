package com.skymilk.chatapp.store.presentation.screen.main.profileEdit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skymilk.chatapp.store.domain.usecase.storage.StorageUseCases
import com.skymilk.chatapp.store.domain.usecase.user.UserUseCases
import com.skymilk.chatapp.store.presentation.utils.Event
import com.skymilk.chatapp.store.presentation.utils.sendEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileEditViewModel @Inject constructor(
    private val userUseCases: UserUseCases,
    private val storageUseCases: StorageUseCases
) : ViewModel() {

    private val _profileEditState = MutableStateFlow<ProfileEditState>(ProfileEditState.Initial)
    val profileEditState = _profileEditState.asStateFlow()

    fun onEvent(event: ProfileEditEvent) {
        when (event) {
            is ProfileEditEvent.UpdateUserProfile -> {
                updateUserProfile(
                    event.userId,
                    event.name,
                    event.statusMessage,
                    event.profileImage,
                )
            }
        }
    }

    private fun updateUserProfile(
        userId: String,
        name: String,
        statusMessage: String,
        profileImage: ProfileImage
    ) {
        uploadImage(userId, profileImage) { imageUrl ->
            viewModelScope.launch {
                val result = userUseCases.updateProfile(userId, name, statusMessage, imageUrl)
                when {
                    result.isSuccess -> {
                        sendEvent(Event.Toast("프로필이 업데이트 되었습니다."))

                        _profileEditState.update { ProfileEditState.Success }
                    }

                    result.isFailure -> {
                        sendEvent(
                            Event.Toast(
                                result.exceptionOrNull()?.message ?: "프로필 업데이트에 실패했습니다"
                            )
                        )

                        _profileEditState.update { ProfileEditState.Error }
                    }
                }

            }
        }
    }

    //이미지 업로드
    private fun uploadImage(id: String, profileImage: ProfileImage, onComplete: (String?) -> Unit) {
        viewModelScope.launch {
            _profileEditState.update { ProfileEditState.Loading }

            when (profileImage) {
                //선택된 이미지가 없다면 null 반환
                //null일땐 이미지를 업데이트하지 않는다
                is ProfileImage.Initial -> {
                    onComplete(null)
                    return@launch
                }

                //기본 이미지를 선택했다면 빈 문자열 전달
                //DB의 이미지 URL이 빈 문자열일때 기본 이미지로 설정된다
                is ProfileImage.Default -> {
                    onComplete("")
                    return@launch
                }

                //선택한 이미지가 있다면 이미지를 업로드한다
                is ProfileImage.Custom -> {
                    storageUseCases.saveProfileImage(id, profileImage.imageBitmap)
                        .catch {

                            onComplete(null)
                        }
                        .collect { progress ->
                            if (progress.downloadUrl != null) {
                                onComplete(progress.downloadUrl)
                            }
                        }
                }

                else -> {}
            }
        }
    }
}