package com.skymilk.chatapp.store.domain.repository

import kotlinx.coroutines.flow.Flow

interface UserSettingRepository {

    suspend fun toggleUserAlarmSetting(isAlarmEnabled: Boolean) // 채팅방 알람 설정

    fun getUserAlarmSetting(): Boolean // 유저 설정 정보 동기 가져오기

    fun getUserAlarmSettingAsync(): Flow<Boolean> // 유저 설정 정보 비동기 가져오기


    suspend fun toggleUserDarkModeSetting(isDarkModeEnabled: Boolean) // 유저 다크모드 설정

    fun getUserDarkModeSetting(): Flow<Boolean> // 유저 다크모드 설정 동기 가져오기
}