package com.skymilk.chatapp.store.presentation.screen.main.chatRoomList

import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.skymilk.chatapp.store.domain.usecase.chat.ChatUseCases
import com.skymilk.chatapp.store.domain.usecase.chatRoomSetting.ChatRoomSettingUseCases
import com.skymilk.chatapp.store.domain.usecase.shared.SharedUseCases
import com.skymilk.chatapp.store.presentation.utils.Event
import com.skymilk.chatapp.store.presentation.utils.sendEvent
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChatRoomsViewModel @AssistedInject constructor(
    @Assisted private val userId: String,
    private val chatUseCases: ChatUseCases,
    private val chatRoomSettingUseCases: ChatRoomSettingUseCases,
    private val sharedUseCases: SharedUseCases,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(userId: String): ChatRoomsViewModel
    }

    companion object {
        fun provideFactory(
            assistedFactory: Factory,
            userId: String
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(userId) as T
            }
        }
    }

    //채팅방 목록
    private val _chatRoomsState = MutableStateFlow<ChatRoomsState>(ChatRoomsState.Initial)
    val chatRoomsState = _chatRoomsState.asStateFlow()

    //채팅방 알림 목록
    private val _chatRoomAlarmsDisabled = MutableStateFlow<List<String>>(emptyList())
    val chatRoomAlarmsDisabled = _chatRoomAlarmsDisabled.asStateFlow()

    //읽지 않은 메시지 수
    var unreadMessageCount by mutableIntStateOf(0)
        private set

    init {
        loadChatRooms()

        loadChatRoomSetting()
    }

    fun onEvent(event: ChatRoomsEvent) {
        when (event) {
            is ChatRoomsEvent.LoadChatRooms -> loadChatRooms()
        }
    }

    private fun loadChatRooms() {
        viewModelScope.launch {
            chatUseCases.getChatRooms(userId)
                .onStart {
                    _chatRoomsState.value = ChatRoomsState.Loading // 로딩 상태 설정
                }
                .catch { exception ->
                    val message = "채팅방 목록을 불러오지 못하였습니다."

                    sendEvent(Event.Toast(message))
                    _chatRoomsState.value = ChatRoomsState.Error(message)
                }
                .collect { chatRooms ->
                    _chatRoomsState.value = ChatRoomsState.Success(chatRooms) // 성공 상태 설정

                    //공유 리포지토리에 채팅방 목록 저장
                    sharedUseCases.setSharedChatRooms(chatRooms)

                    //읽지 않은 메시지 수 저장
                    unreadMessageCount = chatRooms.sumOf { it.unreadCount }
                }
        }
    }

    private fun loadChatRoomSetting() {
        viewModelScope.launch {
            chatRoomSettingUseCases.getAlarmsSetting().collectLatest { setting ->
                _chatRoomAlarmsDisabled.update { setting }
            }
        }
    }
}

