package com.skymilk.chatapp.store.domain.usecase.setting

data class SettingUseCases(
    val getAlarmSetting: GetAlarmSetting,
    val getAlarmSettingAsync: GetAlarmSettingAsync,
    val getAlarmsSetting: GetAlarmsSetting,
    val saveAlarmSetting: SaveAlarmSetting,
    val deleteAlarmSetting: DeleteAlarmSetting
)
