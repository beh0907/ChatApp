package com.skymilk.chatapp.store.presentation.screen.main.chatRoomList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.skymilk.chatapp.store.domain.usecase.chat.ChatUseCases
import com.skymilk.chatapp.store.presentation.utils.Event
import com.skymilk.chatapp.store.presentation.utils.sendEvent
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch


class ChatRoomListViewModel @AssistedInject constructor(
    @Assisted private val userId: String,
    private val chatUseCases: ChatUseCases
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(userId: String): ChatRoomListViewModel
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

    private val _chatRoomsState = MutableStateFlow<ChatRoomsState>(ChatRoomsState.Initial)
    val chatRoomsState: StateFlow<ChatRoomsState> = _chatRoomsState.asStateFlow()

    init {
        loadChatRooms()
    }

    fun loadChatRooms() {
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
}

