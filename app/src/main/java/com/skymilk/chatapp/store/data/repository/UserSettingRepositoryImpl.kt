package com.skymilk.chatapp.store.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.skymilk.chatapp.store.data.utils.Constants.PreferencesKeys
import com.skymilk.chatapp.store.domain.repository.UserSettingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserSettingRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : UserSettingRepository {

    //유저 알람설정 저장하기
    override suspend fun toggleUserAlarmSetting(isAlarmEnabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.USER_ALARM_SETTING_KEY] = isAlarmEnabled
        }
    }

    //유저 알람설정 비동기 가져오기
    override fun getUserAlarmSetting(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[PreferencesKeys.USER_ALARM_SETTING_KEY] != false
        }
    }

    //다크모드 설정
    override suspend fun toggleUserDarkModeSetting(isDarkModeEnabled: Boolean) {
        dataStore.edit { preferences ->
            // 저장
            preferences[PreferencesKeys.USER_DARK_MODE_SETTING_KEY] = isDarkModeEnabled
        }
    }

    //다크모드 설정 가져오기
    override fun getUserDarkModeSetting(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[PreferencesKeys.USER_DARK_MODE_SETTING_KEY] == true
        }
    }
}