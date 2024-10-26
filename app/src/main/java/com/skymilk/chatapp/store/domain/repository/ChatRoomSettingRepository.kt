package com.skymilk.chatapp.store.domain.repository

import kotlinx.coroutines.flow.Flow

interface ChatRoomSettingRepository {

    suspend fun saveAlarmSetting(chatRoomId: String) // 채팅방 알람 설정

    suspend fun deleteAlarmSetting(chatRoomId: String) // 채팅방 알람 설정 제거

    fun getAlarmSetting(chatRoomId: String): Flow<Boolean> // 채팅방 알람 설정 여부 비동기

    fun getAlarmsSetting(): Flow<List<String>> // 전체 채팅방 알람 설정 목록
}