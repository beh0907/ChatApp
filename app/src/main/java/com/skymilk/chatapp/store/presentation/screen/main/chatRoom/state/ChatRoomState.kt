package com.skymilk.chatapp.store.presentation.screen.main.chatRoom.state

import com.skymilk.chatapp.store.domain.model.ChatMessage
import com.skymilk.chatapp.store.domain.model.ChatRoomWithUsers
import org.checkerframework.checker.units.qual.C


sealed class ChatRoomState {
    data object Initial : ChatRoomState() //기본 상태
    data object Loading : ChatRoomState() //로딩 상태
    data class Success(val chatRoom: ChatRoomWithUsers) : ChatRoomState() // 채팅방 목록 로드 성공
    data object Error : ChatRoomState() // 채팅방 목록 에러
}