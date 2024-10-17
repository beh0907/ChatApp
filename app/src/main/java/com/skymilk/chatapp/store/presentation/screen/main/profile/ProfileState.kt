package com.skymilk.chatapp.store.presentation.screen.main.profile

sealed interface ProfileState {

    data object Initial : ProfileState // 기본 상태

    data object Loading : ProfileState // 친구 설정 중

    data class Success(val isFriend: Boolean) : ProfileState //친구 여부 확인

    data object Error : ProfileState // 친구 설정 여부 에러
}
