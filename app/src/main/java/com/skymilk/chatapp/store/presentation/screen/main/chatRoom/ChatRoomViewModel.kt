package com.skymilk.chatapp.store.presentation.screen.main.chatRoom

import android.net.Uri
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.google.firebase.messaging.FirebaseMessaging
import com.skymilk.chatapp.store.data.utils.Constants
import com.skymilk.chatapp.store.domain.model.MessageType
import com.skymilk.chatapp.store.domain.model.Participant
import com.skymilk.chatapp.store.domain.model.User
import com.skymilk.chatapp.store.domain.usecase.chat.ChatUseCases
import com.skymilk.chatapp.store.domain.usecase.chatRoomSetting.ChatRoomSettingUseCases
import com.skymilk.chatapp.store.domain.usecase.storage.StorageUseCases
import com.skymilk.chatapp.store.presentation.navigation.routes.Routes
import com.skymilk.chatapp.store.presentation.screen.main.chatRoom.state.ChatMessagesState
import com.skymilk.chatapp.store.presentation.screen.main.chatRoom.state.ChatRoomState
import com.skymilk.chatapp.store.presentation.screen.main.chatRoom.state.ImageUploadState
import com.skymilk.chatapp.store.presentation.utils.Event
import com.skymilk.chatapp.store.presentation.utils.sendEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class ChatRoomViewModel @Inject constructor(
    private val chatUseCases: ChatUseCases,
    private val storageUseCases: StorageUseCases,
    private val chatRoomSettingUseCases: ChatRoomSettingUseCases,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val chatRoomId: String = savedStateHandle.toRoute<Routes.ChatRoomScreen>().chatRoomId
    private val userId: String = savedStateHandle.toRoute<Routes.ChatRoomScreen>().userId

    //채팅방
    private val _chatRoomState = MutableStateFlow<ChatRoomState>(ChatRoomState.Initial)
    val chatRoomState = _chatRoomState.asStateFlow()

    //채팅 목록
    private val _chatMessagesState = MutableStateFlow<ChatMessagesState>(ChatMessagesState.Initial)
    val chatMessagesState = _chatMessagesState.asStateFlow()

    //이미지 업로드 상태
    private val _uploadState = MutableStateFlow<ImageUploadState>(ImageUploadState.Initial)
    val uploadState = _uploadState.asStateFlow()

    //알람 상태
    //저장된 값이 알람 비활성화
    val alarmState = chatRoomSettingUseCases.getAlarmSetting(chatRoomId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    init {
        onEvent(ChatRoomEvent.LoadChatRoom)

        onEvent(ChatRoomEvent.LoadChatMessages)

        onEvent(ChatRoomEvent.UnsubscribeForNotification)
    }


    fun onEvent(event: ChatRoomEvent) {
        when (event) {
            is ChatRoomEvent.LoadChatRoom -> loadChatRoom()
            is ChatRoomEvent.LoadChatMessages -> loadChatMessages()

            is ChatRoomEvent.SendMessage -> {
                sendMessage(
                    sender = event.sender,
                    content = event.content,
                    participants = event.participants,
                    type = event.type
                )
            }

            is ChatRoomEvent.SendImageMessage -> {
                sendImageMessage(
                    sender = event.sender,
                    imageUris = event.imageUris,
                    participants = event.participants
                )
            }

            is ChatRoomEvent.ExitChatRoom -> {
                exitChatRoom(
                    user = event.user,
                    onNavigateToBack = event.onNavigateToBack
                )
            }

            is ChatRoomEvent.ToggleAlarmState -> toggleAlarmState()
            is ChatRoomEvent.SubscribeForNotification -> subscribeForNotification()
            is ChatRoomEvent.UnsubscribeForNotification -> unsubscribeForNotification()
        }
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

                    _chatRoomState.value = ChatRoomState.Error
                }
                .collect { chatRoom ->
                    _chatRoomState.value = ChatRoomState.Success(chatRoom)
                }

        }
    }

    //채팅 메시지 목록 불러오기
    private fun loadChatMessages() {
        viewModelScope.launch {
            chatUseCases.getRealtimeMessages(chatRoomId)
                .onStart {
                    _chatMessagesState.value = ChatMessagesState.Loading
                }
                .catch { exception ->
                    sendEvent(Event.Toast(exception.message ?: "Unknown error"))

                    _chatMessagesState.value = ChatMessagesState.Error
                }
                .collect { messages ->
                    //메시지 목록 적용
                    _chatMessagesState.value = ChatMessagesState.Success(messages)

                    Log.d("viewmodel", "나야~ 들기름")

                    //업데이트
                    if (chatRoomState.value is ChatRoomState.Success) {
                        val chatRoom = (chatRoomState.value as ChatRoomState.Success).chatRoom
                        val originParticipantStatus =
                            chatRoom.participants.first { it.participantStatus.userId == userId }.participantStatus
                        val updateParticipantStatus = originParticipantStatus.copy(
                            lastReadTimestamp = System.currentTimeMillis(),
                            lastReadMessageCount = chatRoom.totalMessagesCount + 1
                        )

                        //유저 상태정보 갱신
                        chatUseCases.updateParticipantsStatus(
                            chatRoomId,
                            userId,
                            originParticipantStatus,
                            updateParticipantStatus,
                        )
                    }
                }
        }
    }

    //텍스트 메시지 전송
    private fun sendMessage(
        sender: User,
        content: String,
        participants: List<Participant>,
        type: MessageType
    ) {
        viewModelScope.launch {
            try {
                chatUseCases.sendMessage(chatRoomId, sender, content, participants, type)
            } catch (e: Exception) {
                //에러 처리
            }
        }
    }

    //이미지 메시지 전송
    private fun sendImageMessage(
        sender: User,
        imageUris: List<Uri>,
        participants: List<Participant>
    ) {
        //이미지 업로드 결과
        uploadImage(chatRoomId, imageUris) { urls ->
            if (urls.isEmpty()) {
                sendEvent(Event.Toast("이미지 업로드에 실패하였습니다."))
                return@uploadImage
            }

            viewModelScope.launch {
                try {
                    chatUseCases.sendImageMessage(chatRoomId, sender, urls, participants)
                } catch (e: Exception) {
                    _uploadState.value =
                        ImageUploadState.Error("Failed to send image message: ${e.message}")
                }
            }

        }
    }

    //이미지 업로드
    private fun uploadImage(
        chatRoomId: String,
        imageUris: List<Uri>,
        onCompleted: (List<String>) -> Unit
    ) {
        viewModelScope.launch {
            //스토리지 업로드 작업
            storageUseCases.saveChatMessageImage(chatRoomId, imageUris)
                .catch { e ->
                    _uploadState.update {
                        ImageUploadState.Error(
                            e.message ?: "Unknown error occurred"
                        )
                    }
                }
                .collect { imageUploadInfoList ->
                    //이미지 업로드 성공 or 실패 처리 완료된 이미지 수 확인
                    val completedOrFailedImages =
                        imageUploadInfoList.count { it.downloadUrl != null || it.error != null }

                    //업로드 완료 여부 확인
                    _uploadState.update {
                        if (completedOrFailedImages == imageUris.size) {
                            val successfulUploads =
                                imageUploadInfoList.filter { it.downloadUrl != null }
                            val failedUploads = imageUploadInfoList.filter { it.error != null }

                            //업로드가 완료된 이미지 경로 전달
                            onCompleted(successfulUploads.mapNotNull { it.downloadUrl })

                            //완료 되었다면 완료된 이미지 url로 업데이트
                            ImageUploadState.Completed(
                                successfulUploads = successfulUploads,
                                failedUploads = failedUploads
                            )
                        } else {
                            //현재 업로드중인 상태 저장
                            ImageUploadState.Progress(
                                imageUploadInfoList = imageUploadInfoList.toList(), // 새 인스턴스 생성하여 방출 처리
                                completedOrFailedImages = completedOrFailedImages
                            )
                        }
                    }

                    Log.d("uploadImage", "_uploadState: ${_uploadState.value}")
                }
        }
    }

    //채팅방 나가기
    private fun exitChatRoom(user: User, onNavigateToBack: () -> Unit) {
        viewModelScope.launch {
                val chatRoom = (chatRoomState.value as ChatRoomState.Success).chatRoom
                val participantStatus = chatRoom.participants.first { it.participantStatus.userId == userId }.participantStatus

            val result = chatUseCases.exitChatRoom(chatRoomId, userId, participantStatus)

            when {
                result.isSuccess -> {
                    //퇴장 시스템 메시지 전송
                    //시스템 메시지는 알림을 표시하지 않기 위해 emptyList 전송
                    sendMessage(
                        user,
                        "${user.username}님이 퇴장하셨습니다.",
                        emptyList(),
                        MessageType.SYSTEM
                    )

                    //알림 토픽 구독 제거
                    onEvent(ChatRoomEvent.UnsubscribeForNotification)

                    //알림 메시지 출력
                    sendEvent(Event.Toast("채팅방에서 퇴장하였습니다."))

                    //화면 이동
                    onNavigateToBack()
                }

                result.isFailure -> {
                    sendEvent(Event.Toast("채팅방을 나가지 못하였습니다."))
                }
            }
        }
    }

    //알림 상태 저장
    private fun toggleAlarmState() {
        viewModelScope.launch {
            when (alarmState.value) {
                true -> {
                    chatRoomSettingUseCases.deleteAlarmSetting(chatRoomId)
                    sendEvent(Event.Toast("채팅방 알람이 설정되었습니다"))
                }

                false -> {
                    chatRoomSettingUseCases.saveAlarmSetting(chatRoomId)
                    sendEvent(Event.Toast("채팅방 알람이 해제되었습니다"))
                }
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

