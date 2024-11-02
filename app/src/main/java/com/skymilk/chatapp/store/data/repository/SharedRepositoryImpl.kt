package com.skymilk.chatapp.store.data.repository

import com.skymilk.chatapp.store.data.dto.ChatRoom
import com.skymilk.chatapp.store.data.dto.User
import com.skymilk.chatapp.store.domain.model.ChatRoomWithParticipants
import com.skymilk.chatapp.store.domain.model.NavigationState
import com.skymilk.chatapp.store.domain.repository.SharedRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class SharedRepositoryImpl @Inject constructor() : SharedRepository {
    //현재 화면 정보
    private val currentDestination = MutableStateFlow(NavigationState())

    //친구 목록
    private val _friends = MutableStateFlow<List<User>>(emptyList())

    //채팅방 목록
    private val _chatRooms = MutableStateFlow<List<ChatRoomWithParticipants>>(emptyList())

    override fun getCurrentDestination(): NavigationState = currentDestination.value

    override suspend fun setCurrentDestination(navigationState: NavigationState) {
        currentDestination.value = navigationState
    }

    override fun getFriends(): Flow<List<User>> = _friends

    override suspend fun setFriends(friends: List<User>) {
        _friends.value = friends
    }

    override fun getSelectedChatRoom(chatRoomId: String): Flow<ChatRoomWithParticipants?> {
        return _chatRooms.map { chatRooms ->
            chatRooms.find { it.id == chatRoomId }
        }
    }

    override suspend fun setChatRooms(chatRooms: List<ChatRoomWithParticipants>) {
        _chatRooms.value = chatRooms
    }
}



