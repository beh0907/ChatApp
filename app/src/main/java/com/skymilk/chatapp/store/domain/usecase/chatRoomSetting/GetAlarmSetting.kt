package com.skymilk.chatapp.store.domain.usecase.chatRoomSetting

import android.util.Log
import com.skymilk.chatapp.store.domain.repository.ChatRoomSettingRepository
import kotlinx.coroutines.flow.Flow

class GetAlarmSetting(
    private val chatRoomSettingRepository: ChatRoomSettingRepository
) {
    operator fun invoke(chatRoomId: String): Flow<Boolean> {

        Log.d("getAlarmSetting", "4-1")
        return chatRoomSettingRepository.getAlarmSetting(chatRoomId)
    }
}