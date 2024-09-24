package com.skymilk.chatapp.store.presentation.screen.main.profile

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
class ProfileViewModel @Inject constructor(
    private val chatUseCases: ChatUseCases
) : ViewModel() {

    private val _mySoloChatRoomId = MutableStateFlow<String?>(null)
    val mySoloChatRoomId = _mySoloChatRoomId.asStateFlow()

    fun initMySoloChatRoomId() {
        _mySoloChatRoomId.value = null
    }

    fun getMySoloChatRoomId(myUid: String) {
        viewModelScope.launch {
            //나만 있는 (솔로 채팅방) 여부
            //없다면 내부에서 채팅방 생성
            val result = chatUseCases.getOrCreateChatRoom(listOf(myUid))

            when {
                result.isSuccess ->  {
                    _mySoloChatRoomId.value = result.getOrNull()!!
                }
                result.isFailure -> {
                    //토스트 메시지 전달
                    sendEvent(Event.Toast(result.exceptionOrNull()?.message.toString()))
                }
            }
        }
    }

}