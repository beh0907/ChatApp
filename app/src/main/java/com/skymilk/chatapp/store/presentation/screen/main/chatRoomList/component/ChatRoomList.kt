package com.skymilk.chatapp.store.presentation.screen.main.chatRoomList.component

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import com.skymilk.chatapp.store.domain.model.ChatRoomWithUsers
import com.skymilk.chatapp.store.domain.model.User

@Composable
fun ChatRoomList(
    chatRooms: List<ChatRoomWithUsers>,
    currentUser: User,
    chatRoomAlarmsDisabled: List<String>,
    onNavigateToChatRoom: (String) -> Unit
    ) {
    LazyColumn {
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