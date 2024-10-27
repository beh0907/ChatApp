package com.skymilk.chatapp.store.domain.repository

import kotlinx.coroutines.flow.Flow

interface UserSettingRepository {

    suspend fun toggleUserAlarmSetting(isAlarmEnabled: Boolean) // 채팅방 알람 설정

    fun getUserAlarmSetting(): Flow<Boolean> // 유저 설정 정보 가져오기


    suspend fun toggleUserDarkModeSetting(isDarkModeEnabled: Boolean) // 유저 다크모드 설정

    fun getUserDarkModeSetting(): Flow<Boolean> // 유저 다크모드 설정 가져오기


    suspend fun setRefuseIgnoringOptimization() // 배터리 최적화 해제 요청 다시 묻지 않기

    fun getRefuseIgnoringOptimization(): Flow<Boolean> // 배터리 최적화 해제 요청 거부 여부 가져오기
}