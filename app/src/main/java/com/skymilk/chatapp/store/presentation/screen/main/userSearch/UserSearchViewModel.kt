package com.skymilk.chatapp.store.presentation.screen.main.userSearch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skymilk.chatapp.store.domain.usecase.user.UserUseCases
import com.skymilk.chatapp.store.presentation.screen.main.setting.SettingEvent
import com.skymilk.chatapp.store.presentation.utils.Event
import com.skymilk.chatapp.store.presentation.utils.sendEvent
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

    fun onEvent(event: UserSearchEvent) {
        when (event) {
            is UserSearchEvent.UserSearch -> {
                searchUser(event.query)
            }
        }
    }

    private fun searchUser(query: String) {
        viewModelScope.launch {
            val result = userUseCase.searchUser(query)

            when {
                result.isSuccess -> {
                    _userSearchState.value = UserSearchState.Success(result.getOrNull() ?: emptyList())
                }

                result.isFailure -> {
                    val message = "유저 검색에 실패하였습니다."

                    sendEvent(Event.Toast(message))
                    _userSearchState.value = UserSearchState.Error(message)
                }
            }
        }
    }

}