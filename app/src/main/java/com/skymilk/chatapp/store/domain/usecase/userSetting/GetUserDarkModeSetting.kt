package com.skymilk.chatapp.store.domain.usecase.userSetting

import com.skymilk.chatapp.store.domain.repository.ChatRoomSettingRepository
import com.skymilk.chatapp.store.domain.repository.UserSettingRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserDarkModeSetting(
    private val userSettingRepository: UserSettingRepository
) {
    operator fun invoke(): Flow<Boolean> {
        return userSettingRepository.getUserDarkModeSetting()
    }

}