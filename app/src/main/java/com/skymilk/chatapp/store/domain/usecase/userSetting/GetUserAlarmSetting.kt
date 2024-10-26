package com.skymilk.chatapp.store.domain.usecase.userSetting

import com.skymilk.chatapp.store.domain.repository.UserSettingRepository
import kotlinx.coroutines.flow.Flow

class GetUserAlarmSetting(
    private val userSettingRepository: UserSettingRepository
) {
    operator fun invoke(): Flow<Boolean> {
        return userSettingRepository.getUserAlarmSetting()
    }

}