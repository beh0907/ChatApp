package com.skymilk.chatapp.store.presentation.screen.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skymilk.chatapp.store.domain.usecase.AuthUseCases
import com.skymilk.chatapp.store.presentation.screen.AuthState
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
        val currentUser = authUseCases.getCurrentUser()
        _authState.value = if (currentUser != null) {
            AuthState.Authenticated(currentUser)
        } else {
            AuthState.Unauthenticated
        }
    }

    //구글 로그인
    fun signInWithGoogle() {
        viewModelScope.launch {
            _authState.update { AuthState.Loading }

            val result = authUseCases.signInWithGoogle()
            _authState.update {
                when {
                    result.isSuccess -> AuthState.Authenticated(result.getOrNull()!!)
                    result.isFailure -> AuthState.Error(
                        result.exceptionOrNull()?.message ?: "Unknown error"
                    )

                    else -> AuthState.Unauthenticated
                }
            }
        }
    }

    //이메일,패스워드 계정정보 로그인
    fun signInWithEmailAndPassword(email: String, password: String) {
        viewModelScope.launch {
            _authState.update { AuthState.Loading }

            val result = authUseCases.signInWithEmailAndPassword(email, password)
            _authState.update {
                when {
                    result.isSuccess -> AuthState.Authenticated(result.getOrNull()!!)
                    result.isFailure -> AuthState.Error(
                        result.exceptionOrNull()?.message ?: "Unknown error"
                    )

                    else -> AuthState.Unauthenticated
                }
            }
        }
    }

    //로그아웃
    fun signOut() {
        viewModelScope.launch {
            authUseCases.signOut()
            _authState.update { AuthState.Unauthenticated }
        }
    }
}