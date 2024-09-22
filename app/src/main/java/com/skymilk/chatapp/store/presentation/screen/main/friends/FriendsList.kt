package com.skymilk.chatapp.store.presentation.screen.main.friends

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import com.skymilk.chatapp.store.domain.model.ChatRoomWithUsers
import com.skymilk.chatapp.store.domain.model.User
import com.skymilk.chatapp.store.presentation.screen.main.chatRoomList.ChatRoomItem
import com.skymilk.chatapp.store.presentation.screen.main.chatRoomList.ChatRoomItemShimmer

@Composable
fun FriendsList(
    friends: List<User>,
) {
    LazyColumn {
        items(friends, key = { friend -> friend.id }) { friend ->
            FriendsItem(friend, onUserItemClick = {})
        }
    }
}

@Composable
fun FriendsListShimmer() {
    LazyColumn {
        items(20) {
            FriendsItemShimmer()
        }
    }
}