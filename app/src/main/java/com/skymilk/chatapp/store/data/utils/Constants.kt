package com.skymilk.chatapp.store.data.utils

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object Constants {
    const val FCM_SERVER_URL = "https://fcm.googleapis.com/"

    const val FCM_TOPIC_PREFIX = "ChatRoom_"

    const val SETTING_DATA_STORE = "SETTING_DATA_STORE" // Datastore 이름

    const val DISABLE_ALARM_SETTING_CHATROOM_KEY =
        "DISABLE_ALARM_SETTING_CHATROOM_KEY" // Datastore 채팅방 알람 비활성화 설정 키
    const val USER_ALARM_SETTING_KEY = "USER_ALARM_SETTING_KEY" // Datastore 유저 알람 설정 키
    const val USER_DARK_MODE_SETTING_KEY = "USER_DARK_MODE_SETTING_KEY" // Datastore 유저 다크모드 설정 키
    const val USER_REFUSE_IGNORE_OPTIMIZATION_KEY =
        "USER_REFUSE_IGNORE_OPTIMIZATION_KEY" // 배터리 최적화 해제 요청 거부 키

    //파이어베이스 레퍼런스
    object FirebaseReferences {
        //FireStore
        const val CHATROOM = "chatRooms"
        const val FRIENDS = "friends"
        const val USERS = "users"

        //RTDB
        const val CHAT_STATUS = "chatStatus"
        const val MESSAGES = "messages"

        //Storage
        const val CHAT_IMAGES = "chat_images"
        const val PROFILE_IMAGES = "profile_images"
    }

    //dataStore 키
    object PreferencesKeys {
        val DISABLE_ALARM_SETTING_CHATROOM_KEY = stringPreferencesKey(name = Constants.DISABLE_ALARM_SETTING_CHATROOM_KEY)

        val USER_ALARM_SETTING_KEY = booleanPreferencesKey(name = Constants.USER_ALARM_SETTING_KEY)

        val USER_DARK_MODE_SETTING_KEY = booleanPreferencesKey(name = Constants.USER_DARK_MODE_SETTING_KEY)

        val USER_REFUSE_IGNORE_OPTIMIZATION_KEY = booleanPreferencesKey(name = Constants.USER_REFUSE_IGNORE_OPTIMIZATION_KEY)
    }
}