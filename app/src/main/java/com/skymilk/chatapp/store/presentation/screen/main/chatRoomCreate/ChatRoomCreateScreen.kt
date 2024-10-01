package com.skymilk.chatapp.store.presentation.screen.main.chatRoomCreate

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.skymilk.chatapp.store.domain.model.User
import com.skymilk.chatapp.store.presentation.screen.main.friends.FriendsState
import com.skymilk.chatapp.ui.theme.LeeSeoYunFont

@Composable
fun ChatRoomCreateScreen(
    modifier: Modifier = Modifier,
    friendsState: FriendsState,
    viewModel: ChatRoomCreateViewModel = hiltViewModel(),
    currentUser: User,
    onNavigateToChatRoom: (String) -> Unit,
    onNavigateToBack: () -> Unit
) {
    var selectedUsers by remember { mutableStateOf(listOf<User>()) }
    var searchQuery by remember { mutableStateOf("") }

    val chatRoomId by viewModel.chatRoomId.collectAsStateWithLifecycle()

    LaunchedEffect(chatRoomId) {
        chatRoomId?.let {
            onNavigateToChatRoom(it)
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        //상단 타이틀 섹션
        TopSection(
            currentUser = currentUser,
            selectedUsers = selectedUsers,
            onNavigateToBack = onNavigateToBack,
            onCreateChatRoom = viewModel::getChatRoomId
        )

        //선택된 유저 목록
        SelectedUserList(
            selectedUsers = selectedUsers,
            onUserRemove = { user ->
                selectedUsers = selectedUsers - user
            }
        )

        //유저 닉네임 검색 섹션
        FriendSearchSection(
            searchQuery = searchQuery,
            onSearchQueryChange = { searchQuery = it }
        )

        when(friendsState) {
            is FriendsState.Success -> {
                //유저 목록 섹션
                SelectionUserList(
                    users = friendsState.friends,
                    selectedUsers = selectedUsers,
                    searchQuery = searchQuery,
                    onUserSelect = { user ->
                        selectedUsers = if (user in selectedUsers) {
                            selectedUsers - user
                        } else {
                            listOf(user) + selectedUsers
                        }
                    }
                )
            }

            else -> {

            }
        }



    }
}

//상단 타이틀
@Composable
fun TopSection(
    currentUser: User,
    selectedUsers: List<User>,
    onNavigateToBack: () -> Unit,
    onCreateChatRoom: (List<String>) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = {
            onNavigateToBack()
        }) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface
            )
        }

        Text(
            modifier = Modifier.weight(1f),
            text = "대화상대 초대",
            fontFamily = LeeSeoYunFont,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface
        )

        IconButton(
            enabled = selectedUsers.isNotEmpty(),
            onClick = {
                onCreateChatRoom(selectedUsers.map { it.id } + currentUser.id)
            }
        ) {
            Icon(Icons.Default.Check, contentDescription = "Confirm")
        }
    }
}

@Composable
private fun FriendSearchSection(
    modifier: Modifier = Modifier,
    searchQuery : String,
    onSearchQueryChange: (String) -> Unit
) {

    // 검색어 입력 칸
    TextField(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.primaryContainer),
        value = searchQuery,
        onValueChange = onSearchQueryChange,
        singleLine = true,
        placeholder = {
            Text(
                text = "이름을 검색해주세요.",
                fontFamily = LeeSeoYunFont,
                color = Color.Gray,
            )
        },
        trailingIcon = {
            // 초기화 버튼
            if (searchQuery.isEmpty()) return@TextField
            IconButton(
                onClick = {
                    onSearchQueryChange("")
                }
            ) {
                Icon(Icons.Default.Cancel, contentDescription = null)
            }
        },
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent
        ),
        textStyle = TextStyle(
            fontFamily = LeeSeoYunFont,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
    )
}