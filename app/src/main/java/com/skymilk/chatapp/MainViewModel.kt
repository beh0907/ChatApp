package com.skymilk.chatapp

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

    private val _ignoringOptimizationState = MutableStateFlow<Boolean>(true)
    val ignoringOptimizationState = _ignoringOptimizationState

    init {
        onEvent(MainEvent.GetTheme)

        onEvent(MainEvent.GetRefuseIgnoringOptimization)
    }

    fun onEvent(event: MainEvent) {
        when(event) {
            is MainEvent.GetTheme -> getTheme()

            is MainEvent.GetRefuseIgnoringOptimization -> getRefuseIgnoringOptimization()

            is MainEvent.SetRefuseIgnoringOptimization -> setRefuseIgnoringOptimization()
        }
    }

    private fun getTheme() {
        viewModelScope.launch {
            userSettingUseCases.getUserDarkModeSetting().collectLatest {
                _darkModeState.value = it
            }
        }
    }

    private fun getRefuseIgnoringOptimization() {
        viewModelScope.launch {
            userSettingUseCases.getRefuseIgnoringOptimization().collectLatest {
                _ignoringOptimizationState.value = it
            }
        }
    }

    private fun setRefuseIgnoringOptimization() {
        viewModelScope.launch {
            userSettingUseCases.setRefuseIgnoringOptimization()
        }
    }
}