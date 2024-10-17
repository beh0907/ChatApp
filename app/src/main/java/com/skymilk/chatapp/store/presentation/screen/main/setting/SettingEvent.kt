package com.skymilk.chatapp.store.presentation.screen.main.setting

sealed interface SettingEvent {

    data object LoadSetting : SettingEvent

    data object ToggleAlarmSetting : SettingEvent

    data object ToggleDarkModeSetting : SettingEvent

}