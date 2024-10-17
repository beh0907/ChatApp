package com.skymilk.chatapp.store.domain.usecase.chatRoomSetting

import com.skymilk.chatapp.store.domain.repository.ChatRoomSettingRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAlarmsSetting(
    private val chatRoomSettingRepository: ChatRoomSettingRepository
) {
    operator fun invoke(): Flow<List<String>> {
        return chatRoomSettingRepository.getAlarmsSetting()
    }

}