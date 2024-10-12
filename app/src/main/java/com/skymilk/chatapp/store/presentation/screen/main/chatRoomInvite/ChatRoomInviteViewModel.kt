package com.skymilk.chatapp.store.presentation.screen.main.chatRoomInvite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skymilk.chatapp.store.domain.model.MessageType
import com.skymilk.chatapp.store.domain.model.User
import com.skymilk.chatapp.store.domain.usecase.chat.ChatUseCases
import com.skymilk.chatapp.utils.Event
import com.skymilk.chatapp.utils.sendEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatRoomInviteViewModel @Inject constructor(
    private val chatUseCases: ChatUseCases,
) : ViewModel() {

    //채팅방 이동 아이디
    private val _chatRoomId = MutableStateFlow<String?>(null)
    val chatRoomId = _chatRoomId.asStateFlow()

    //파라미터와 일치한 참여자가 있는 채팅방 찾기
    fun getChatRoomId(currentUser: User, participants: List<User>) {
        viewModelScope.launch {
            //유저 아이디가 일치한 채팅방의 아이디값을 가져온다
            //없다면 내부에서 채팅방 생성
            val result =
                chatUseCases.getOrCreateChatRoom(participants.map { it.id } + currentUser.id)

            //결과 처리
            handleResult(
                currentUser = currentUser,
                participants = participants,
                result = result
            )
        }
    }

    fun addParticipantsToChatRoom(
        currentUser: User,
        chatRoomId: String,
        participants: List<User>
    ) {
        viewModelScope.launch {
            val result = chatUseCases.addParticipants(chatRoomId, participants.map { it.id })

            //결과 처리
            handleResult(
                currentUser = currentUser,
                participants = participants,
                result = result
            )
        }
    }

    private suspend fun handleResult(
        currentUser: User,
        participants: List<User>,
        result: Result<String>
    ) {
        when {
            result.isSuccess -> {
                val chatRoomId = result.getOrThrow()

                // 초대 메시지 내용 생성
                val participantNames = participants.joinToString("님, ") { it.username }
                val content = "${currentUser.username}님이 ${participantNames}님을 초대하셨습니다."

                //초대 시스템 메시지 추가
                chatUseCases.sendMessage(
                    chatRoomId,
                    currentUser,
                    content,
                    participants,
                    MessageType.SYSTEM
                )

                //채팅방 아이디 설정
                //채팅방 아이디가 설정되면 채팅방으로 이동한다
                _chatRoomId.value = chatRoomId
            }

            result.isFailure -> {
                //토스트 메시지 전달
                sendEvent(Event.Toast(result.exceptionOrNull()?.message.toString()))
            }
        }
    }
}