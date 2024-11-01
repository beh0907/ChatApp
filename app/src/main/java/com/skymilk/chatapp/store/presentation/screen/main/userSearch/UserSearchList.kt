package com.skymilk.chatapp.store.presentation.screen.main.userSearch

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.skymilk.chatapp.store.data.dto.User

@Composable
fun UserSearchList(
    currentUser: User,
    users: List<User>,
    onUserItemClick: (User) -> Unit,
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(users, key = { user -> user.id }) { user ->

            //내 정보를 필터링하여 표시하지 않도록 처리
            if (user.id == currentUser.id) return@items

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