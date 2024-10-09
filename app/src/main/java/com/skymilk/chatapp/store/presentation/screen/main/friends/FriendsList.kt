package com.skymilk.chatapp.store.presentation.screen.main.friends

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skymilk.chatapp.store.domain.model.User
import com.skymilk.chatapp.ui.theme.CookieRunFont

@Composable
fun FriendsList(
    friends: List<User>,
    onUserItemClick: (User) -> Unit,
) {
    LazyColumn {
        item {
            Text(
                modifier = Modifier.padding(top = 10.dp, start = 15.dp),
                text = "친구 ${friends.size}",
                fontFamily = CookieRunFont,
                fontSize = 14.sp
            )
        }

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