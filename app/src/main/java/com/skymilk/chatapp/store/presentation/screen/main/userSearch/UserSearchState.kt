package com.skymilk.chatapp.store.presentation.screen.main.userSearch

import com.skymilk.chatapp.store.data.dto.User

sealed interface UserSearchState {

    data object Initial : UserSearchState // 기본 상태

    data object Loading : UserSearchState // 유저 검색 중

    data class Success(val users: List<User>) : UserSearchState // 유저 검색 결과

    data class Error(val message: String) : UserSearchState // 유저 검색 에러
}
