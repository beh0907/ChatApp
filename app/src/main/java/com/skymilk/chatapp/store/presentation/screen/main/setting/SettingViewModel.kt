package com.skymilk.chatapp.store.presentation.screen.main.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skymilk.chatapp.store.domain.usecase.userSetting.UserSettingUseCases
import com.skymilk.chatapp.store.presentation.utils.Event
import com.skymilk.chatapp.store.presentation.utils.sendEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val userSettingUseCases: UserSettingUseCases
) : ViewModel() {

    private val _settingState = MutableStateFlow(SettingState())
    val settingState = _settingState.asStateFlow()

    init {
        onEvent(SettingEvent.LoadSetting)
    }

    fun onEvent(event: SettingEvent) {
        when (event) {
            is SettingEvent.LoadSetting -> {
                loadSetting()
            }

            is SettingEvent.ToggleAlarmSetting -> {
                toggleAlarmSetting()
            }

            is SettingEvent.ToggleDarkModeSetting -> {
                toggleDarkModeSetting()
            }
        }
    }

    private fun loadSetting() {
        viewModelScope.launch {
            userSettingUseCases.getUserAlarmSettingAsync().collectLatest { isAlarmEnabled ->
                _settingState.update {
                    it.copy(isAlarmEnabled = isAlarmEnabled)
                }
            }
        }

        viewModelScope.launch {
            userSettingUseCases.getUserDarkModeSetting().collectLatest { isDarkModeEnabled ->
                _settingState.update {
                    it.copy(isDarkModeEnabled = isDarkModeEnabled != false)
                }
            }
        }
    }

    private fun toggleAlarmSetting() {
        viewModelScope.launch {
            val isAlarmEnabled = !settingState.value.isAlarmEnabled

            userSettingUseCases.toggleUserAlarmSetting(isAlarmEnabled)

            //토스트 메시지 전달
            sendEvent(Event.Toast("알림을 ${if (isAlarmEnabled) "설정" else "해제"}하였습니다."))
        }
    }

    private fun toggleDarkModeSetting() {
        viewModelScope.launch {
            val isDarkModeEnabled = !settingState.value.isDarkModeEnabled

            userSettingUseCases.toggleUserDarkModeSetting(isDarkModeEnabled)

            //토스트 메시지 전달
            sendEvent(Event.Toast("어두운 모드를 ${if (isDarkModeEnabled) "설정" else "해제"}하였습니다."))
        }
    }
}