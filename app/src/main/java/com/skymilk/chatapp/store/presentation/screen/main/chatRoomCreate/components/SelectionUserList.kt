package com.skymilk.chatapp.store.presentation.screen.main.chatRoomCreate.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skymilk.chatapp.store.domain.model.User
import com.skymilk.chatapp.ui.theme.LeeSeoYunFont

@Composable
fun SelectionUserList(
    users: List<User>,
    selectedUsers: List<User>,
    searchQuery: String,
    onUserSelect: (User) -> Unit
) {
    val filteredUsers = users.filter {
        it.username.contains(searchQuery, ignoreCase = true)
    }

    LazyColumn(
        modifier = Modifier.padding(top = 5.dp)
    ) {
        item {
            Text(
                modifier = Modifier.padding(start = 15.dp),
                text = "친구 ${filteredUsers.size}",
                fontFamily = LeeSeoYunFont,
                fontSize = 14.sp
            )
        }

        items(filteredUsers) { user ->
            SelectionUserItem(
                user = user,
                isSelected = user in selectedUsers,
                onSelect = { onUserSelect(user) }
            )
        }
    }
}