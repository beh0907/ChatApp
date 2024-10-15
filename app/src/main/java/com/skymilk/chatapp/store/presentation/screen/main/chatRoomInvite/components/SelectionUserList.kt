package com.skymilk.chatapp.store.presentation.screen.main.chatRoomInvite.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.skymilk.chatapp.store.domain.model.User

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
                text = "초대 가능한 친구 ${filteredUsers.size}",
                style = MaterialTheme.typography.bodyMedium,
            )
        }

        items(filteredUsers) { user ->
            SelectionUserItem(
                user = user,
                isSelected = user in selectedUsers,
                onUserSelect = onUserSelect
            )
        }
    }
}