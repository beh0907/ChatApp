package com.skymilk.chatapp.store.domain.repository

import com.skymilk.chatapp.store.data.dto.ChatRoom
import com.skymilk.chatapp.store.data.dto.User
import com.skymilk.chatapp.store.domain.model.ChatRoomWithParticipants
import com.skymilk.chatapp.store.domain.model.NavigationState
import kotlinx.coroutines.flow.Flow

interface SharedRepository {
    fun getCurrentDestination(): NavigationState

    suspend fun setCurrentDestination(navigationState: NavigationState)

    fun getFriends(): Flow<List<User>>

    suspend fun setFriends(friends: List<User>)

    fun getSelectedChatRoom(chatRoomId: String): Flow<ChatRoomWithParticipants?>

    suspend fun setChatRooms(chatRooms: List<ChatRoomWithParticipants>)

}