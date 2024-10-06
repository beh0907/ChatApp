package com.skymilk.chatapp.store.domain.usecase.setting

import com.skymilk.chatapp.store.domain.repository.SettingRepository
import javax.inject.Inject

class DeleteAlarmSetting @Inject constructor(
    private val settingRepository: SettingRepository
) {
    suspend operator fun invoke(chatRoomId: String) {
        settingRepository.deleteAlarmSetting(chatRoomId)
    }

}