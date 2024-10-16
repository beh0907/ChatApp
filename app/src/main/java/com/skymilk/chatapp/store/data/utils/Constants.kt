package com.skymilk.chatapp.store.data.utils

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object Constants {
    const val FCM_SERVER_URL = "https://fcm.googleapis.com/"

    const val FCM_TOPIC_PREFIX = "ChatRoom_"

    const val SETTING_DATA_STORE = "SETTING_DATA_STORE" // Datastore 이름

    const val DISABLE_ALARM_SETTING_CHATROOM_KEY = "ALARM_SETTING_CHATROOM_KEY" // Datastore 채팅방 알람 비활성화 설정 키
    const val USER_ALARM_SETTING_KEY = "USER_SETTING_KEY" // Datastore 유저 설정 키
    const val CURRENT_DESTINATION_KEY = "USER_SETTING_KEY" // Datastore 현재 화면 상태 키

    //dataStore 키
    object PreferencesKeys {
        val DISABLE_ALARM_SETTING_CHATROOM_KEY = stringPreferencesKey(name = Constants.DISABLE_ALARM_SETTING_CHATROOM_KEY)
        val USER_ALARM_SETTING_KEY = booleanPreferencesKey(name = Constants.USER_ALARM_SETTING_KEY)
        val CURRENT_DESTINATION_KEY = stringPreferencesKey(name = Constants.CURRENT_DESTINATION_KEY)
    }
}