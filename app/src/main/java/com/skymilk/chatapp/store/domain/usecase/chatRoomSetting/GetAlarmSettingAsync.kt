package com.skymilk.chatapp.store.domain.usecase.chatRoomSetting

import com.skymilk.chatapp.store.domain.repository.ChatRoomSettingRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAlarmSettingAsync(
    private val chatRoomSettingRepository: ChatRoomSettingRepository
) {
    operator fun invoke(chatRoomId: String): Flow<Boolean> {
        return chatRoomSettingRepository.getAlarmSettingAsync(chatRoomId)
    }
}