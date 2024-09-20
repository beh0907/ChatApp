package com.skymilk.chatapp.store.presentation.screen.main.chatRoom

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.messaging.FirebaseMessaging
import com.skymilk.chatapp.store.domain.model.ChatRoomWithUsers
import com.skymilk.chatapp.store.domain.usecase.chat.ChatUseCases
import com.skymilk.chatapp.store.domain.usecase.storage.StorageUseCases
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


class ChatRoomViewModel @AssistedInject constructor(
    @Assisted private val chatRoom: ChatRoomWithUsers,
    private val chatUseCases: ChatUseCases,
    private val storageUseCases: StorageUseCases
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(chatRoom: ChatRoomWithUsers): ChatRoomViewModel
    }

    companion object {
        fun provideFactory(
            assistedFactory: Factory,
            chatRoom: ChatRoomWithUsers
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(chatRoom) as T
            }
        }
    }

    //채팅 목록
    val chatMessages = chatUseCases.getMessages(chatRoom.id)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    //이미지 업로드 상태
    private val _uploadState = MutableStateFlow<ImageUploadState>(ImageUploadState.Initial)
    val uploadState = _uploadState.asStateFlow()


    fun sendMessage(senderId: String, content: String) {
        viewModelScope.launch {
            try {
                chatUseCases.sendMessage(chatRoom.id, senderId, content)
            } catch (e: Exception) {
                //에러 처리
            }
        }
    }

    fun sendImageMessage(senderId: String, imageUri: Uri) {
        //이미지 업로드 결과
        uploadImage(senderId, imageUri) { url ->

            viewModelScope.launch {
                try {
                    chatUseCases.sendImageMessage(chatRoom.id, senderId, url)
                } catch (e: Exception) {
                    _uploadState.value =
                        ImageUploadState.Error("Failed to send image message: ${e.message}")
                }
            }

        }
    }

    //이미지 업로드
    private fun uploadImage(id: String, uri: Uri, onSuccess: (String) -> Unit) {
        viewModelScope.launch {
            _uploadState.value = ImageUploadState.Loading
            try {
                storageUseCases.saveChatMessageImage(id, uri).collect { progress ->
                    _uploadState.value = when {
                        progress.downloadUrl != null -> {
                            onSuccess(progress.downloadUrl)
                            ImageUploadState.Success(progress.downloadUrl)
                        }

                        else -> ImageUploadState.Uploading(
                            progress = progress.progress,
                            bytesTransferred = progress.bytesTransferred,
                            totalBytes = progress.totalBytes
                        )
                    }
                }
            } catch (e: Exception) {
                _uploadState.value = ImageUploadState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }
}

