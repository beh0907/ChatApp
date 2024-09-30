package com.skymilk.chatapp.store.domain.usecase.setting

import com.skymilk.chatapp.store.domain.repository.SettingRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAlarmSettingAsync @Inject constructor(
    private val settingRepository: SettingRepository
) {
    operator fun invoke(chatRoomId: String): Flow<Boolean> {
        return settingRepository.getAlarmSettingAsync(chatRoomId)
    }
}