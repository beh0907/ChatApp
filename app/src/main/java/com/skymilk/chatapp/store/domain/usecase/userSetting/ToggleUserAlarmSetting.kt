package com.skymilk.chatapp.store.domain.usecase.userSetting

import com.skymilk.chatapp.store.domain.repository.UserSettingRepository

class ToggleUserAlarmSetting(
    private val userSettingRepository: UserSettingRepository
) {
    suspend operator fun invoke(isAlarmEnabled: Boolean) {
        return userSettingRepository.toggleUserAlarmSetting(isAlarmEnabled)
    }

}