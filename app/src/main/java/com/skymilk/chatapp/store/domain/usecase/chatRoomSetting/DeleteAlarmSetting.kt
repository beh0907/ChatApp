package com.skymilk.chatapp.store.domain.usecase.chatRoomSetting

import com.skymilk.chatapp.store.domain.repository.ChatRoomSettingRepository
import javax.inject.Inject

class DeleteAlarmSetting(
    private val chatRoomSettingRepository: ChatRoomSettingRepository
) {
    suspend operator fun invoke(chatRoomId: String) {
        chatRoomSettingRepository.deleteAlarmSetting(chatRoomId)
    }

}