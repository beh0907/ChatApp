package com.skymilk.chatapp.store.domain.usecase.setting

import com.skymilk.chatapp.store.domain.repository.SettingRepository
import javax.inject.Inject

class GetAlarmSetting @Inject constructor(
    private val settingRepository: SettingRepository
) {
    operator fun invoke(chatRoomId: String): Boolean {
        return settingRepository.getAlarmSetting(chatRoomId)
    }

}