package com.skymilk.chatapp.store.domain.usecase.userSetting

import com.skymilk.chatapp.store.domain.repository.ChatRoomSettingRepository
import com.skymilk.chatapp.store.domain.repository.UserSettingRepository
import javax.inject.Inject

class GetUserAlarmSetting(
    private val userSettingRepository: UserSettingRepository
) {
    operator fun invoke(): Boolean {
        return userSettingRepository.getUserAlarmSetting()
    }

}