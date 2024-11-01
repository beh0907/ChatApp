package com.skymilk.chatapp.store.presentation.screen.auth

import com.skymilk.chatapp.store.data.dto.User

sealed interface AuthState {
    
    data object Initial : AuthState // 기본 초기 상태
    
    data object Loading : AuthState // 로딩 상태
    
    data class Authenticated(val user: User) : AuthState // 로그인 성공
    
    data object Unauthenticated : AuthState // 유저 정보 없을 때
    
    data class Error(val message: String) : AuthState // 로그인 실패
}