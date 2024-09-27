package com.skymilk.chatapp.store.presentation.screen.main.userSearch

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.skymilk.chatapp.store.domain.model.User
import com.skymilk.chatapp.store.presentation.screen.main.friends.FriendsItem
import com.skymilk.chatapp.store.presentation.screen.main.friends.FriendsItemShimmer

@Composable
fun UserSearchList(
    users: List<User>,
    onUserItemClick: (User) -> Unit,
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(users, key = { user -> user.id }) { user ->
            UserSearchItem(user, onUserItemClick = onUserItemClick)
        }
    }
}

@Composable
fun UserSearchListShimmer() {
    LazyColumn {
        items(20) {
            UserSearchItemShimmer()
        }
    }
}