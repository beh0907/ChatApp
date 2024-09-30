package com.skymilk.chatapp.store.domain.usecase.setting

import com.skymilk.chatapp.store.domain.model.ChatMessage
import com.skymilk.chatapp.store.domain.repository.ChatRepository
import com.skymilk.chatapp.store.domain.repository.SettingRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SaveAlarmSetting @Inject constructor(
    private val settingRepository: SettingRepository
) {
    suspend operator fun invoke(chatRoomId: String) {
        settingRepository.saveAlarmSetting(chatRoomId)
    }

}