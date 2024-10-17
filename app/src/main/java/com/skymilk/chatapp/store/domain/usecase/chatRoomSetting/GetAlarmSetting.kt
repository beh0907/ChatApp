package com.skymilk.chatapp.store.domain.usecase.chatRoomSetting

import com.skymilk.chatapp.store.domain.repository.ChatRoomSettingRepository
import javax.inject.Inject

class GetAlarmSetting(
    private val chatRoomSettingRepository: ChatRoomSettingRepository
) {
    operator fun invoke(chatRoomId: String): Boolean {
        return chatRoomSettingRepository.getAlarmSetting(chatRoomId)
    }

}