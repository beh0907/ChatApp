package com.skymilk.chatapp.store.presentation.screen.main.chatRoomList.component

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.skymilk.chatapp.store.domain.model.ChatRoomWithParticipants
import com.skymilk.chatapp.store.data.dto.User

@Composable
fun ChatRoomList(
    chatRooms: List<ChatRoomWithParticipants>,
    currentUser: User,
    chatRoomAlarmsDisabled: List<String>,
    onNavigateToChatRoom: (String) -> Unit
    ) {
    LazyColumn(
        modifier = Modifier.padding(bottom = 10.dp)
    ) {
        items(chatRooms, key = { chatRoom -> chatRoom.id }) { chatRoom ->
            when (chatRoom.participants.size) {
                1 -> ChatSoloRoomItem(chatRoom, currentUser, onNavigateToChatRoom) // 나의 채팅방
                else -> ChatRoomItem(
                    chatRoom,
                    currentUser,
                    chatRoomAlarmsDisabled.contains(chatRoom.id),
                    onNavigateToChatRoom
                ) // 한명 이상의 대상이 있는 채팅방
            }
        }
    }
}

@Composable
fun ChatRoomListShimmer() {
    LazyColumn {
        items(20) {
            ChatRoomItemShimmer()
        }
    }
}