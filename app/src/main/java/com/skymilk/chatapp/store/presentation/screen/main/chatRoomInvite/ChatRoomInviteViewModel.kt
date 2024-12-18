package com.skymilk.chatapp.store.presentation.screen.main.chatRoomInvite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skymilk.chatapp.store.data.dto.ParticipantStatus
import com.skymilk.chatapp.store.data.dto.MessageType
import com.skymilk.chatapp.store.data.dto.User
import com.skymilk.chatapp.store.domain.usecase.chat.ChatUseCases
import com.skymilk.chatapp.store.domain.usecase.shared.SharedUseCases
import com.skymilk.chatapp.store.presentation.screen.main.friends.FriendsState
import com.skymilk.chatapp.store.presentation.utils.Event
import com.skymilk.chatapp.store.presentation.utils.sendEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatRoomInviteViewModel @Inject constructor(
    private val chatUseCases: ChatUseCases,
    private val sharedUseCases: SharedUseCases
) : ViewModel() {

    private val _friendsState = MutableStateFlow<FriendsState>(FriendsState.Initial)
    val friendsState = _friendsState.asStateFlow()

    init {
        loadFriends()
    }

    fun onEvent(event: ChatRoomInviteEvent) {
        when(event) {
            is ChatRoomInviteEvent.GetChatRoomId -> {
                getChatRoomId(
                    currentUser = event.currentUser,
                    participants = event.participants,
                    onNavigateToChatRoom = event.onNavigateToChatRoom
                )
            }

            is ChatRoomInviteEvent.AddParticipants -> {
                addParticipants(
                    currentUser = event.currentUser,
                    chatRoomId = event.chatRoomId,
                    participants = event.participants,
                    onNavigateToChatRoom = event.onNavigateToChatRoom
                )
            }
        }
    }

    //공유 리포지토리로부터 친구 목록 로드
    private fun loadFriends() {
        viewModelScope.launch {
            sharedUseCases.getSharedFriends().collectLatest {
                _friendsState.value = FriendsState.Success(it)
            }
        }
    }

    //파라미터와 일치한 참여자가 있는 채팅방 찾기
    private fun getChatRoomId(
        currentUser: User,
        participants: List<User>,
        onNavigateToChatRoom: (String) -> Unit
    ) {
        viewModelScope.launch {
            //새로운 채팅방을 만든다
            //이미 중복된 참여자의 채팅방이 있더라도 새로 만든다
            val result =
                chatUseCases.createChatRoom("새로운 채팅방", participants.map { it.id } + currentUser.id)

            //결과 처리
            handleResult(
                currentUser = currentUser,
                participants = participants,
                result = result,
                onNavigateToChatRoom = onNavigateToChatRoom
            )
        }
    }

    private fun addParticipants(
        currentUser: User,
        chatRoomId: String,
        participants: List<User>,
        onNavigateToChatRoom: (String) -> Unit
    ) {
        viewModelScope.launch {
            val result = chatUseCases.addParticipants(chatRoomId, participants.map { it.id })

            //결과 처리
            handleResult(
                currentUser = currentUser,
                participants = participants,
                result = result,
                onNavigateToChatRoom = onNavigateToChatRoom
            )
        }
    }

    private suspend fun handleResult(
        currentUser: User,
        participants: List<User>,
        result: Result<String>,
        onNavigateToChatRoom: (String) -> Unit
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
                    User(),
                    content,
                    emptyList(),
                    MessageType.SYSTEM,
                    ParticipantStatus(), //
                )

                //채팅방으로 이동한다
                onNavigateToChatRoom(chatRoomId)
            }

            result.isFailure -> {
                //토스트 메시지 전달
                sendEvent(Event.Toast(result.exceptionOrNull()?.message.toString()))
            }
        }
    }
}