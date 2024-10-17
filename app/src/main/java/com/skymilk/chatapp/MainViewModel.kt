package com.skymilk.chatapp

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skymilk.chatapp.store.domain.usecase.userSetting.UserSettingUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userSettingUseCases: UserSettingUseCases
) : ViewModel() {

    private val _darkModeState = MutableStateFlow<Boolean>(false)
    val darkModeState = _darkModeState

    init {
        onEvent(MainEvent.GetTheme)
    }

    fun onEvent(event: MainEvent) {
        when(event) {
            is MainEvent.GetTheme -> getTheme()
        }
    }

    private fun getTheme() {
        viewModelScope.launch {
            userSettingUseCases.getUserDarkModeSetting().collectLatest {
                _darkModeState.value = it
            }
        }
    }
}