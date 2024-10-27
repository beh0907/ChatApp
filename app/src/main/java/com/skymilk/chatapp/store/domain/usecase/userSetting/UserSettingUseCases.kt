package com.skymilk.chatapp.store.domain.usecase.userSetting

data class UserSettingUseCases(
    //유저 알람 설정
    val getUserAlarmSetting: GetUserAlarmSetting,
    val toggleUserAlarmSetting: ToggleUserAlarmSetting,

    //유저 다크모드 설정
    val getUserDarkModeSetting: GetUserDarkModeSetting,
    val toggleUserDarkModeSetting: ToggleUserDarkModeSetting,

    //배터리 최적화 해제 요청 여부 설정
    val getRefuseIgnoringOptimization: GetRefuseIgnoringOptimization,
    val setRefuseIgnoringOptimization: SetRefuseIgnoringOptimization,
)
