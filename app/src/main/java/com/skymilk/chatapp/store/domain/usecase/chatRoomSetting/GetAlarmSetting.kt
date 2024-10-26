package com.skymilk.chatapp.store.domain.usecase.chatRoomSetting

import com.skymilk.chatapp.store.domain.repository.ChatRoomSettingRepository
import kotlinx.coroutines.flow.Flow

class GetAlarmSetting(
    private val chatRoomSettingRepository: ChatRoomSettingRepository
) {
    operator fun invoke(chatRoomId: String): Flow<Boolean> {
        return chatRoomSettingRepository.getAlarmSetting(chatRoomId)
    }
}