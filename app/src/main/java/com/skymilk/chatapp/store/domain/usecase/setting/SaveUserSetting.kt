package com.skymilk.chatapp.store.domain.usecase.setting

import com.skymilk.chatapp.store.domain.repository.SettingRepository
import javax.inject.Inject

class SaveUserSetting @Inject constructor(
    private val settingRepository: SettingRepository
) {
    suspend operator fun invoke(isAlarmEnabled: Boolean) {
        return settingRepository.saveUserSetting(isAlarmEnabled)
    }

}