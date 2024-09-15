package com.skymilk.chatapp.store.presentation.screen.auth.signUp

//회원가입 시 검증 처리
sealed class RegisterValidation {
    object Success : RegisterValidation()
    data class Failed(val message: String) : RegisterValidation()
}

//입력 정보 검증 상태
data class RegisterFieldState(
    val email: RegisterValidation,
    val password: RegisterValidation
)