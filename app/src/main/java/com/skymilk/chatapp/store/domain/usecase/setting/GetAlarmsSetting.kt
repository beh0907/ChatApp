package com.skymilk.chatapp.store.domain.usecase.setting

import com.skymilk.chatapp.store.domain.repository.SettingRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAlarmsSetting @Inject constructor(
    private val settingRepository: SettingRepository
) {
    operator fun invoke(): Flow<List<String>> {
        return settingRepository.getAlarmsSetting()
    }

}