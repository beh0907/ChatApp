package com.skymilk.chatapp.store.presentation.screen.main.friends

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import com.skymilk.chatapp.store.domain.model.User

@Composable
fun FriendsList(
    friends: List<User>,
    onUserItemClick: (User) -> Unit,
) {
    LazyColumn {
        items(friends, key = { friend -> friend.id }) { friend ->
            FriendsItem(friend, onUserItemClick = {
                onUserItemClick(friend)
            })
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