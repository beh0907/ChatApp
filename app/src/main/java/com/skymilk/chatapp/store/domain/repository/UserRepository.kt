package com.skymilk.chatapp.store.domain.repository

import com.skymilk.chatapp.store.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    suspend fun updateProfile(user: User): Result<Unit> // 프로필 정보 갱신

    suspend fun getUser(userId: String): Result<User> // 특정 유저 정보 가져오기

    fun getFriends(userId: String): Flow<List<User>> // 나의 친구 목록 가져오기
}