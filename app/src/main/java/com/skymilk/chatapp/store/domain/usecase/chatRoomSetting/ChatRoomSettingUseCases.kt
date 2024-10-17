package com.skymilk.chatapp.store.domain.usecase.chatRoomSetting

import com.skymilk.chatapp.store.domain.usecase.userSetting.GetUserAlarmSetting
import com.skymilk.chatapp.store.domain.usecase.userSetting.GetUserAlarmSettingAsync
import com.skymilk.chatapp.store.domain.usecase.userSetting.ToggleUserAlarmSetting

data class ChatRoomSettingUseCases(
    //채팅방 알람 설정
    val getAlarmSetting: GetAlarmSetting,
    val getAlarmSettingAsync: GetAlarmSettingAsync,
    val getAlarmsSetting: GetAlarmsSetting,
    val saveAlarmSetting: SaveAlarmSetting,
    val deleteAlarmSetting: DeleteAlarmSetting,
)
