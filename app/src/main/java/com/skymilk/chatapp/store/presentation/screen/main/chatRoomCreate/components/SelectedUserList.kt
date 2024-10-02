package com.skymilk.chatapp.store.presentation.screen.main.chatRoomCreate.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.skymilk.chatapp.store.domain.model.User

//선택된 유저 섹션
@Composable
fun SelectedUserList(
    selectedUsers: List<User>,
    onUserRemove: (User) -> Unit
) {
    AnimatedVisibility(
        visible = selectedUsers.isNotEmpty(),
    ) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            items(selectedUsers) { user ->

                AnimatedVisibility(
                    visible = true,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    SelectedUserItem(
                        user = user,
                        onUserRemove = onUserRemove
                    )
                }
            }
        }
    }
}