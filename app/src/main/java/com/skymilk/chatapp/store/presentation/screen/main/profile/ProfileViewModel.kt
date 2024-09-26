package com.skymilk.chatapp.store.presentation.screen.main.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skymilk.chatapp.store.domain.usecase.chat.ChatUseCases
import com.skymilk.chatapp.store.domain.usecase.user.UserUseCases
import com.skymilk.chatapp.utils.Event
import com.skymilk.chatapp.utils.sendEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val chatUseCases: ChatUseCases,
    private val userUseCases: UserUseCases
) : ViewModel() {

    //채팅방 이동 아이디
    private val _chatRoomId = MutableStateFlow<String?>(null)
    val chatRoomId = _chatRoomId.asStateFlow()

    //타인의 프로필에 대해 친구 추가/삭제 상태
    private val _isFriendState = MutableStateFlow<FriendState>(FriendState.Initial)
    val isFriendState = _isFriendState.asStateFlow()

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

    //친구 추가/삭제 상태 가져오기
    fun getFriendState(myUserId: String, otherUserId: String) {
        viewModelScope.launch {
            userUseCases.getIsFriend(myUserId, otherUserId)
                .onEach {
                    //시작 시 로딩 상태
                    _isFriendState.value = FriendState.Loading
                }.collect { isFriend ->
                    //친구 여부 상태를 저장
                    _isFriendState.value = FriendState.Success(isFriend)
                }
        }
    }

    fun setFriendState(myUserId: String, otherUserId: String, isFriend: Boolean) {
        viewModelScope.launch {
            userUseCases.setFriend(myUserId, otherUserId, !isFriend)

            //토스트 메시지 전달
            sendEvent(Event.Toast(
                if (isFriend) "친구 삭제 완료" else "친구 추가 완료"
            ))
        }
    }
}