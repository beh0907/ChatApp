package com.skymilk.chatapp.store.presentation.screen.main.profile

sealed interface FriendState {

    data object Initial : FriendState // 기본 상태

    data object Loading : FriendState // 친구 설정 중

    data class Success(val isFriend: Boolean) : FriendState //친구 여부 확인

    data object Error : FriendState // 친구 설정 여부 에러
}
