package com.skymilk.chatapp.store.presentation.screen.main.chatRoomList

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.skymilk.chatapp.store.domain.usecase.chat.ChatUseCases
import com.skymilk.chatapp.store.domain.usecase.chatRoomSetting.ChatRoomSettingUseCases
import com.skymilk.chatapp.store.presentation.navigation.routes.Routes
import com.skymilk.chatapp.store.presentation.utils.Event
import com.skymilk.chatapp.store.presentation.utils.sendEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ChatRoomsViewModel @Inject constructor(
    private val chatUseCases: ChatUseCases,
    private val chatRoomSettingUseCases: ChatRoomSettingUseCases,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val userId: String = savedStateHandle.toRoute<Routes.ChatRoomsScreen>().userId

    private val _chatRoomsState = MutableStateFlow<ChatRoomsState>(ChatRoomsState.Initial)
    val chatRoomsState: StateFlow<ChatRoomsState> = _chatRoomsState.asStateFlow()

    private val _chatRoomAlarmsDisabled = MutableStateFlow<List<String>>(listOf())
    val chatRoomAlarmsDisabled = _chatRoomAlarmsDisabled.asStateFlow()

    init {
        onEvent(ChatRoomsEvent.LoadChatRooms)

        onEvent(ChatRoomsEvent.LoadChatRoomSetting)
    }

    fun onEvent(event: ChatRoomsEvent) {
        when (event) {
            is ChatRoomsEvent.LoadChatRooms -> loadChatRooms()

            is ChatRoomsEvent.LoadChatRoomSetting -> loadChatRoomSetting()
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

