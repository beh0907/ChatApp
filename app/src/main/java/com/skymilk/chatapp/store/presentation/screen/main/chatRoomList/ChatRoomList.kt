package com.skymilk.chatapp.store.presentation.screen.main.chatRoomList

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import com.skymilk.chatapp.store.domain.model.ChatRoomWithUsers
import com.skymilk.chatapp.store.domain.model.User

@Composable
fun ChatRoomList(
    chatRooms: List<ChatRoomWithUsers>,
    currentUser: User,
    onChatItemClick: (String) -> Unit
) {
    LazyColumn {
        items(chatRooms, key = { chatRoom -> chatRoom.id }) { chatRoom ->
            ChatRoomItem(chatRoom, currentUser, onChatItemClick)
        }
    }
}

@Composable
fun ChatRoomListShimmer() {
    LazyColumn {
        items(10) {
            ChatRoomItemShimmer()
        }
    }
}