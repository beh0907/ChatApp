package com.skymilk.chatapp.store.presentation.screen.main.chatRoomList

import com.skymilk.chatapp.store.domain.model.ChatRoomWithUsers

sealed class ChatRoomsState {
    data object Initial : ChatRoomsState() //기본 상태
    data object Loading : ChatRoomsState() //로딩 상태
    data class Success(val chatRooms: List<ChatRoomWithUsers>) : ChatRoomsState() // 채팅방 목록 로드 성공
    data class Error(val message: String) : ChatRoomsState() // 채팅방 목록 로드 실패
}
