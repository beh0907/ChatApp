package com.skymilk.chatapp.store.domain.usecase.setting

data class SettingUseCases(
    //채팅방 알람 설정
    val getAlarmSetting: GetAlarmSetting,
    val getAlarmSettingAsync: GetAlarmSettingAsync,
    val getAlarmsSetting: GetAlarmsSetting,
    val saveAlarmSetting: SaveAlarmSetting,
    val deleteAlarmSetting: DeleteAlarmSetting,

    //유저 설정
    val getUserSetting: GetUserSetting,
    val getUserSettingAsync: GetUserSettingAsync,
    val saveUserSetting: SaveUserSetting,
)
