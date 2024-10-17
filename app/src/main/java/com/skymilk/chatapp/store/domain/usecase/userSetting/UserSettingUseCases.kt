package com.skymilk.chatapp.store.domain.usecase.userSetting

data class UserSettingUseCases(
    val getUserAlarmSetting: GetUserAlarmSetting,
    val getUserAlarmSettingAsync: GetUserAlarmSettingAsync,
    val toggleUserAlarmSetting: ToggleUserAlarmSetting,

    val getUserDarkModeSetting: GetUserDarkModeSetting,
    val toggleUserDarkModeSetting: ToggleUserDarkModeSetting,
)
