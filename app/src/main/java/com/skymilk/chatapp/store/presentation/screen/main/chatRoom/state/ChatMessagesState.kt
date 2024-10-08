package com.skymilk.chatapp.store.presentation.screen.main.chatRoom.state

import com.skymilk.chatapp.store.domain.model.ChatMessage


sealed interface ChatMessagesState {

    data object Initial : ChatMessagesState //기본 상태

    data object Loading : ChatMessagesState //로딩 상태

    data class Success(val chatMessages: List<ChatMessage>) : ChatMessagesState // 채팅방 목록 로드 성공

    data object Error : ChatMessagesState // 에러 상태
}