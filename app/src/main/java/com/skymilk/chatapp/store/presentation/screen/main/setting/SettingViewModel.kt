package com.skymilk.chatapp.store.presentation.screen.main.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skymilk.chatapp.store.domain.usecase.setting.SettingUseCases
import com.skymilk.chatapp.utils.Event
import com.skymilk.chatapp.utils.sendEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val settingUseCases: SettingUseCases
) : ViewModel() {

    private val _settingState = MutableStateFlow(SettingState())
    val settingState = _settingState.asStateFlow()

    init {
        loadSetting()
    }

    private fun loadSetting() {
        viewModelScope.launch {
            settingUseCases.getUserSettingAsync().collectLatest { isAlarmEnabled ->
                _settingState.update { currentState ->
                    currentState.copy(isAlarmEnabled = isAlarmEnabled)
                }
            }
        }
    }

    fun toggleAlarmSetting(isAlarmEnabled: Boolean) {
        viewModelScope.launch {
            settingUseCases.saveUserSetting(isAlarmEnabled)

            //토스트 메시지 전달
            sendEvent(Event.Toast("알림을 ${if (isAlarmEnabled) "설정" else "해제"}하였습니다."))
        }
    }
}