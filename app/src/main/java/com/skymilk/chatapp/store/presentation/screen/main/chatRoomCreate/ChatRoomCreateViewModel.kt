package com.skymilk.chatapp.store.presentation.screen.main.chatRoomCreate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skymilk.chatapp.store.domain.usecase.chat.ChatUseCases
import com.skymilk.chatapp.utils.Event
import com.skymilk.chatapp.utils.sendEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatRoomCreateViewModel @Inject constructor(
    private val chatUseCases: ChatUseCases,
) : ViewModel() {

    //채팅방 이동 아이디
    private val _chatRoomId = MutableStateFlow<String?>(null)
    val chatRoomId = _chatRoomId.asStateFlow()

    //파라미터와 일치한 참여자가 있는 채팅방 찾기
    fun getChatRoomId(userIds: List<String>) {
        viewModelScope.launch {
            //유저 아이디가 일치한 채팅방의 아이디값을 가져온다
            //없다면 내부에서 채팅방 생성
            val result = chatUseCases.getOrCreateChatRoom(userIds)

            when {
                result.isSuccess -> {
                    _chatRoomId.value = result.getOrNull()!!
                }

                result.isFailure -> {
                    //토스트 메시지 전달
                    sendEvent(Event.Toast(result.exceptionOrNull()?.message.toString()))
                }
            }
        }
    }

}