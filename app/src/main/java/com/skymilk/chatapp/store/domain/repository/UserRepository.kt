package com.skymilk.chatapp.store.domain.repository

import com.skymilk.chatapp.store.data.dto.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    suspend fun updateProfile(
        userId: String,
        name: String,
        statusMessage: String,
        imageUrl: String?
    ): Result<Unit> // 프로필 정보 갱신

    fun updateFcmToken(userId: String, token: String) // FCM 토큰 업데이트

    suspend fun getUser(userId: String): Result<User> // 특정 유저 정보 가져오기

    fun getFriends(userId: String): Flow<List<User>> // 나의 친구 목록 가져오기

    fun getIsFriend(myUserId: String, otherUserId: String): Flow<Boolean> // 친구 여부 상태 정보 가져오기

    suspend fun setFriend(myUserId: String, otherUserId: String, isFriend: Boolean) // 친구 상태 저장하기

    suspend fun searchUser(query: String): Result<List<User>> //유저 정보 검색
}