package com.skymilk.chatapp.store.presentation.screen.main.friends

import com.skymilk.chatapp.store.domain.model.User

sealed class FriendsState {
    data object Initial : FriendsState() //기본 상태
    data object Loading : FriendsState() //로딩 상태
    data class Success(val friends: List<User>) : FriendsState() // 채팅방 목록 로드 성공
}