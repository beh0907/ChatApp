package com.skymilk.chatapp.store.presentation.screen.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skymilk.chatapp.store.domain.model.User
import com.skymilk.chatapp.store.domain.usecase.auth.AuthUseCases
import com.skymilk.chatapp.store.presentation.screen.auth.signUp.RegisterValidation
import com.skymilk.chatapp.utils.ValidationUtil
import com.skymilk.chatapp.utils.Event
import com.skymilk.chatapp.utils.sendEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authUseCases: AuthUseCases
) : ViewModel() {

    //로그인 상태 정보
    private val _authState = MutableStateFlow<AuthState>(AuthState.Initial)
    val authState = _authState.asStateFlow()

    init {
        checkCurrentUser()
    }

    //로그인 정보 체크
    private fun checkCurrentUser() {
        viewModelScope.launch {
            val currentUser = authUseCases.getCurrentUser()

            _authState.update {
                if (currentUser != null) {
                    AuthState.Authenticated(currentUser)
                } else {
                    AuthState.Unauthenticated
                }
            }
        }

    }

    //구글 로그인
    fun signInWithGoogle() {
        viewModelScope.launch {
            _authState.update { AuthState.Loading }

            val result = authUseCases.signInWithGoogle()
            checkSignInResult(result)
        }
    }

    //이메일,패스워드 계정정보 로그인
    fun signInWithEmailAndPassword(email: String, password: String) {
        if (email.isBlank()) {
            //토스트 메시지 전달
            sendEvent(Event.Toast("이메일을 입력해주세요."))
            return
        }

        if (password.isBlank()) {
            //토스트 메시지 전달
            sendEvent(Event.Toast("비밀번호를 입력해주세요."))
            return
        }

        viewModelScope.launch {
            _authState.update { AuthState.Loading }

            val result = authUseCases.signInWithEmailAndPassword(email, password)
            checkSignInResult(result)
        }
    }

    //이메일,패스워드 계정정보 회원가입
    fun signUpWithEmailAndPassword(
        name: String,
        email: String,
        password: String,
        passwordConfirm: String
    ) {
        //이름 입력값 확인
        val validateName = ValidationUtil.validateName(name)
        if (validateName is RegisterValidation.Failed) {
            //토스트 메시지 전달
            sendEvent(Event.Toast(validateName.message))
            return
        }

        //이메일 입력값 확인
        val emailValidation = ValidationUtil.validateEmail(email)
        if (emailValidation is RegisterValidation.Failed) {
            //토스트 메시지 전달
            sendEvent(Event.Toast(emailValidation.message))
            return
        }

        //비밀번호 입력값 확인
        val passwordValidation = ValidationUtil.validatePasswordConfirm(password, passwordConfirm)
        if (passwordValidation is RegisterValidation.Failed) {
            //토스트 메시지 전달
            sendEvent(Event.Toast(passwordValidation.message))
            return
        }

        //입력값 확인 후 회원가입 처리
        viewModelScope.launch {
            _authState.update { AuthState.Loading }

            val result = authUseCases.signUpWithEmailAndPassword(name, email, password)
            checkSignInResult(result)
        }
    }

    //로그아웃
    fun signOut() {
        viewModelScope.launch {
            authUseCases.signOut()
            _authState.update { AuthState.Unauthenticated }

            //토스트 메시지 전달
            sendEvent(Event.Toast("로그아웃 되었습니다."))
        }
    }

    private fun checkSignInResult(result: Result<User>) {
        _authState.update {
            when {
                result.isSuccess -> {
                    //토스트 메시지 전달
                    sendEvent(Event.Toast("로그인에 성공하였습니다."))

                    AuthState.Authenticated(result.getOrNull()!!)
                }

                result.isFailure -> {
                    //토스트 메시지 전달
                    sendEvent(Event.Toast(result.exceptionOrNull()?.message.toString()))

                    AuthState.Error(
                        result.exceptionOrNull()?.message ?: "Unknown error"
                    )
                }

                else -> {
                    AuthState.Unauthenticated
                }
            }
        }
    }
}