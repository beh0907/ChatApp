package com.skymilk.chatapp.store.domain.usecase.chatRoomSetting

data class ChatRoomSettingUseCases(
    //채팅방 알람 설정
    val getAlarmSetting: GetAlarmSetting,
    val getAlarmsSetting: GetAlarmsSetting,
    val saveAlarmSetting: SaveAlarmSetting,
    val deleteAlarmSetting: DeleteAlarmSetting,
)
