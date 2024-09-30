package com.skymilk.chatapp.store.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.skymilk.chatapp.store.domain.repository.SettingRepository
import com.skymilk.chatapp.utils.Constants
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject


//알람 설정은 비활성화된 채팅방을 저장한다
class SettingRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : SettingRepository {

    override suspend fun saveAlarmSetting(chatRoomId: String) {
        context.dataStore.edit { preferences ->
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
        context.dataStore.edit { preferences ->
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

    override fun getAlarmSetting(chatRoomId: String): Boolean {
        //동기 처리를 위해 runBlocking
        return runBlocking {
            context.dataStore.data.map { preferences ->
                //현재 저장된 목록 가져오기
                val currentList =
                    preferences[PreferencesKeys.DISABLE_ALARM_SETTING_CHATROOM_KEY]?.let {
                        Json.decodeFromString<List<String>>(it)
                    } ?: emptyList()

                //리스트 내 포함 여부 체크
                chatRoomId in currentList
            }.first()
        }
    }

    override fun getAlarmSettingAsync(chatRoomId: String): Flow<Boolean> {
        return context.dataStore.data.map { preferences ->
            //현재 저장된 목록 가져오기
            val currentList = preferences[PreferencesKeys.DISABLE_ALARM_SETTING_CHATROOM_KEY]?.let {
                Json.decodeFromString<List<String>>(it)
            } ?: emptyList()

            //리스트 내 포함 여부 체크
            chatRoomId in currentList
        }
    }

    override fun getAlarmsSetting(): Flow<List<String>> {
        //저장된 목록 그대로 반환
        return context.dataStore.data.map { preferences ->
            preferences[PreferencesKeys.DISABLE_ALARM_SETTING_CHATROOM_KEY]?.let {
                Json.decodeFromString(it)
            } ?: emptyList()
        }
    }
}

//dataStore 설정
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = Constants.SETTING_DATA_STORE)

object PreferencesKeys {
    val DISABLE_ALARM_SETTING_CHATROOM_KEY =
        stringPreferencesKey(name = Constants.DISABLE_ALARM_SETTING_CHATROOM_KEY)
}

