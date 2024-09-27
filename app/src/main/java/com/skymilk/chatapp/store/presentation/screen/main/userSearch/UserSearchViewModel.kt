package com.skymilk.chatapp.store.presentation.screen.main.userSearch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skymilk.chatapp.store.domain.usecase.user.UserUseCases
import com.skymilk.chatapp.utils.Event
import com.skymilk.chatapp.utils.sendEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserSearchViewModel @Inject constructor(
    private val userUseCase: UserUseCases
) : ViewModel() {

    private val _userSearchState = MutableStateFlow<UserSearchState>(UserSearchState.Initial)
    val userSearchState = _userSearchState.asStateFlow()

    fun searchUser(query: String, condition: String) {
        viewModelScope.launch {
            val result = userUseCase.searchUser(query,condition)

            when {
                result.isSuccess -> {
                    _userSearchState.value = UserSearchState.Success(result.getOrNull() ?: emptyList())
                }

                result.isFailure -> {
                    sendEvent(Event.Toast("유저 검색에 실패하였습니다."))

                    _userSearchState.value = UserSearchState.Error(result.exceptionOrNull()?.message)
                }
            }
        }
    }

}