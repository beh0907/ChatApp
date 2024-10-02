package com.skymilk.chatapp.store.presentation.screen.auth

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skymilk.chatapp.store.domain.model.User
import com.skymilk.chatapp.store.domain.usecase.auth.AuthUseCases
import com.skymilk.chatapp.store.presentation.screen.auth.signUp.RegisterValidation
import com.skymilk.chatapp.utils.Event
import com.skymilk.chatapp.utils.ValidationUtil
import com.skymilk.chatapp.utils.sendEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
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
            authUseCases.getCurrentUser()
                .catch { exception ->
                    _authState.update {
                        val message = exception.message ?: "Unknown error"
                        //토스트 메시지 전달
                        sendEvent(Event.Toast(message))

                        AuthState.Error(message)
                    }
                }
                .collectLatest { user ->
                    //유저 정보 갱신
                    _authState.update {
                        AuthState.Authenticated(user)
                    }
                }
        }

    }

    //구글 로그인
    fun signInWithGoogle() {
        viewModelScope.launch {
            authUseCases.signInWithGoogle()
                .catch { exception ->
                    _authState.update {
                        val message = exception.message ?: "Unknown error"
                        //토스트 메시지 전달
                        sendEvent(Event.Toast(message))

                        AuthState.Error(message)
                    }
                }
                .collectLatest { user ->
                    checkSignInResult(user)
                }
        }
    }

    //카카오 로그인
    //카카오 로그인 화면 전환 시 activity로 이동하기 때문에 acitivity가 필요하다
    fun signInWithKakao(activity: Activity) {
        viewModelScope.launch {
            authUseCases.signInWithKakao(activity)
                .catch { exception ->
                    _authState.update {
                        val message = exception.message ?: "Unknown error"
                        //토스트 메시지 전달
                        sendEvent(Event.Toast(message))

                        AuthState.Error(message)
                    }
                }
                .collectLatest { user ->
                    checkSignInResult(user)
                }
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
            authUseCases.signInWithEmailAndPassword(email, password)
                .catch { exception ->
                    _authState.update {
                        val message = exception.message ?: "Unknown error"
                        //토스트 메시지 전달
                        sendEvent(Event.Toast(message))

                        AuthState.Error(message)
                    }
                }
                .collectLatest { user ->
                    checkSignInResult(user)
                }
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
            authUseCases.signUpWithEmailAndPassword(name, email, password)
                .catch { exception ->
                    _authState.update {
                        val message = exception.message ?: "Unknown error"
                        //토스트 메시지 전달
                        sendEvent(Event.Toast(message))

                        AuthState.Error(message)
                    }
                }
                .collectLatest { user ->
                    checkSignInResult(user)
                }
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

    private fun checkSignInResult(user: User) {
        //로그인 상태일때 유저정보가 변경되는 경우도 있다
        //로그인 인증 상태로 변할때만 표시한다
        if (_authState.value !is AuthState.Authenticated) {
            sendEvent(Event.Toast("로그인에 성공하였습니다."))
        }

        //유저 정보 갱신
        _authState.update {
            AuthState.Authenticated(user)
        }
    }
}