package com.skymilk.chatapp.store.domain.usecase.userSetting

import com.skymilk.chatapp.store.domain.repository.UserSettingRepository
import javax.inject.Inject

class ToggleUserDarkModeSetting(
    private val userSettingRepository: UserSettingRepository
) {
    suspend operator fun invoke(isDarkModeEnabled: Boolean) {
        return userSettingRepository.toggleUserDarkModeSetting(isDarkModeEnabled)
    }

}