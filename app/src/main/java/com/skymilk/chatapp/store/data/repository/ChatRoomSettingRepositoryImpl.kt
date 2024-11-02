package com.skymilk.chatapp.store.data.repository

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.skymilk.chatapp.store.data.utils.Constants.PreferencesKeys
import com.skymilk.chatapp.store.domain.repository.ChatRoomSettingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject


class ChatRoomSettingRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : ChatRoomSettingRepository {

    //알람 설정은 비활성화된 채팅방을 저장한다
    override suspend fun saveAlarmSetting(chatRoomId: String) {
        dataStore.edit { preferences ->
            //현재 저장된 목록 가져오기
            val currentList = preferences[PreferencesKeys.DISABLE_ALARM_SETTING_CHATROOM_KEY]?.let {
                Json.decodeFromString<List<String>>(it)
            } ?: emptyList()

            //리스트에 파라미터 채팅방 아이디 추가 후 중복제거
            val newList = (currentList + chatRoomId).distinct()

            // 저장
            preferences[PreferencesKeys.DISABLE_ALARM_SETTING_CHATROOM_KEY] =
                Json.encodeToString(newList)
        }
    }

    override suspend fun deleteAlarmSetting(chatRoomId: String) {
        dataStore.edit { preferences ->
            //현재 저장된 목록 가져오기
            val currentList = preferences[PreferencesKeys.DISABLE_ALARM_SETTING_CHATROOM_KEY]?.let {
                Json.decodeFromString<List<String>>(it)
            } ?: emptyList()

            //파라미터 채팅방 아이디 제거
            val newList = currentList.filter { it != chatRoomId }

            // 저장
            preferences[PreferencesKeys.DISABLE_ALARM_SETTING_CHATROOM_KEY] =
                Json.encodeToString(newList)
        }
    }

    override fun getAlarmSetting(chatRoomId: String): Flow<Boolean> {
        Log.d("getAlarmSetting", "4-2")
        return dataStore.data.map { preferences ->

            Log.d("getAlarmSetting", "4-3")

            //현재 저장된 목록 가져오기
            val currentList = preferences[PreferencesKeys.DISABLE_ALARM_SETTING_CHATROOM_KEY]?.let {
                Json.decodeFromString<List<String>>(it)
            } ?: emptyList()

            Log.d("getAlarmSetting", "4-4")
            //리스트 내 포함 여부 체크
            chatRoomId in currentList
        }
    }

    override fun getAlarmsSetting(): Flow<List<String>> {
        //저장된 목록 그대로 반환
        return dataStore.data.map { preferences ->
            preferences[PreferencesKeys.DISABLE_ALARM_SETTING_CHATROOM_KEY]?.let {
                Json.decodeFromString(it)
            } ?: emptyList()
        }
    }
}

