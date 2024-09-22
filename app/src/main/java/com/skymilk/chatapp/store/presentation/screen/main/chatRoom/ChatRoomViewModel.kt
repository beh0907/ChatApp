package com.skymilk.chatapp.store.presentation.screen.main.chatRoom

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.messaging.FirebaseMessaging
import com.skymilk.chatapp.store.domain.usecase.chat.ChatUseCases
import com.skymilk.chatapp.store.domain.usecase.storage.StorageUseCases
import com.skymilk.chatapp.store.presentation.screen.main.chatRoom.state.ChatMessagesState
import com.skymilk.chatapp.store.presentation.screen.main.chatRoom.state.ChatRoomState
import com.skymilk.chatapp.store.presentation.screen.main.chatRoom.state.ImageUploadState
import com.skymilk.chatapp.utils.Constants
import com.skymilk.chatapp.utils.Event
import com.skymilk.chatapp.utils.sendEvent
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ChatRoomViewModel @AssistedInject constructor(
    @Assisted private val chatRoomId: String,
    private val chatUseCases: ChatUseCases,
    private val storageUseCases: StorageUseCases
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(chatRoomId: String): ChatRoomViewModel
    }

    companion object {
        fun provideFactory(
            assistedFactory: Factory,
            chatRoom: String
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(chatRoom) as T
            }
        }
    }

    //채팅방
    private val _chatRoomState = MutableStateFlow<ChatRoomState>(ChatRoomState.Initial)
    val chatRoomState = _chatRoomState.asStateFlow()

    //채팅 목록
    private val _chatMessagesState = MutableStateFlow<ChatMessagesState>(ChatMessagesState.Initial)
    val chatMessagesState = _chatMessagesState.asStateFlow()

    //이미지 업로드 상태
    private val _uploadState = MutableStateFlow<ImageUploadState>(ImageUploadState.Initial)
    val uploadState = _uploadState.asStateFlow()

    init {
        loadChatRoom()

        loadChatMessages()

        subscribeForNotification()
    }

    //채팅방 정보 불러오기
    private fun loadChatRoom() {
        viewModelScope.launch {
            chatUseCases.getChatRoom(chatRoomId)
                .onStart {
                    _chatRoomState.value = ChatRoomState.Loading
                }
                .catch { exception ->
                    sendEvent(Event.Toast(exception.message ?: "Unknown error"))
                }
                .collect { chatRoom ->
                    _chatRoomState.value = ChatRoomState.Success(chatRoom)
                }

        }
    }

    //채팅 메시지 목록 불러오기
    private fun loadChatMessages() {
        viewModelScope.launch {
            chatUseCases.getMessages(chatRoomId)
                .onStart {
                    _chatMessagesState.value = ChatMessagesState.Loading
                }
                .catch { exception ->
                    sendEvent(Event.Toast(exception.message ?: "Unknown error"))
                }
                .collect { messages ->
                    _chatMessagesState.value = ChatMessagesState.Success(messages)
                }
        }
    }

    fun sendMessage(senderId: String, content: String) {
        viewModelScope.launch {
            try {
                chatUseCases.sendMessage(chatRoomId, senderId, content)
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
                    chatUseCases.sendImageMessage(chatRoomId, senderId, url)
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
            _uploadState.value = ImageUploadState.Loading(imageUri = uri)
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
                            totalBytes = progress.totalBytes,
                            imageUri = uri
                        )
                    }
                }
            } catch (e: Exception) {
                _uploadState.value = ImageUploadState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    //fcm 알림 토픽 등록
    private fun subscribeForNotification() {
        viewModelScope.launch {
            FirebaseMessaging.getInstance()
                .subscribeToTopic("${Constants.FCM_TOPIC_PREFIX}${chatRoomId}")
                .await()
        }


    }

    //fcm 알림 토픽 제거
    private fun unsubscribeForNotification() {
        viewModelScope.launch {
            FirebaseMessaging.getInstance()
                .unsubscribeFromTopic("${Constants.FCM_TOPIC_PREFIX}${chatRoomId}").await()
        }
    }
}

