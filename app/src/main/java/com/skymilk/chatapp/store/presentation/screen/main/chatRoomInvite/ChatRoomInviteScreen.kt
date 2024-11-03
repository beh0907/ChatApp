package com.skymilk.chatapp.store.presentation.screen.main.chatRoomInvite

import android.R.id.message
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.skymilk.chatapp.store.data.dto.User
import com.skymilk.chatapp.store.presentation.screen.main.chatRoomInvite.components.SelectedUserList
import com.skymilk.chatapp.store.presentation.screen.main.chatRoomInvite.components.SelectionUserList
import com.skymilk.chatapp.store.presentation.screen.main.friends.FriendsState
import kotlin.text.isEmpty

@Composable
fun ChatRoomInviteScreen(
    modifier: Modifier = Modifier,
    viewModel: ChatRoomInviteViewModel = hiltViewModel(),
    onEvent: (ChatRoomInviteEvent) -> Unit,
    currentUser: User,
    existingChatRoomId: String?,
    existingParticipants: List<String>,
    onNavigateToChatRoom: (String) -> Unit,
    onNavigateToBack: () -> Unit
) {
    val friendsState by viewModel.friendsState.collectAsStateWithLifecycle()

    var selectedUsers by remember { mutableStateOf(listOf<User>()) }
    var searchQuery by remember { mutableStateOf("") }


    Column(modifier = modifier.fillMaxSize()) {
        //상단 타이틀 섹션
        TopSection(selectedUsers = selectedUsers,
            onNavigateToBack = onNavigateToBack,
            onCreateChatRoom = {
                if (existingChatRoomId != null)
                //이미 채팅방이 있다면 참여자만 추가한다
                    onEvent(
                        ChatRoomInviteEvent.AddParticipants(
                            currentUser = currentUser,
                            chatRoomId = existingChatRoomId,
                            participants = selectedUsers,
                            onNavigateToChatRoom = onNavigateToChatRoom
                        )
                    )
                else
                //채팅방을 새로 생성한다
                    onEvent(
                        ChatRoomInviteEvent.GetChatRoomId(
                            currentUser = currentUser,
                            participants = selectedUsers,
                            onNavigateToChatRoom = onNavigateToChatRoom
                        )
                    )
            })

        //선택된 유저 목록
        SelectedUserList(selectedUsers = selectedUsers, onUserRemove = { user ->
            selectedUsers = selectedUsers - user
        })

        //유저 닉네임 검색 섹션
        FriendSearchSection(searchQuery = searchQuery, onSearchQueryChange = { searchQuery = it })

        when (friendsState) {
            is FriendsState.Success -> {
                //기존 채팅방일 경우 해당 참가자들은 제외하여 표시한다
                val availableUsers =
                    if (existingChatRoomId != null) (friendsState as FriendsState.Success).friends.filter { it.id !in existingParticipants }
                    else (friendsState as FriendsState.Success).friends


                //유저 목록 섹션
                SelectionUserList(users = availableUsers,
                    selectedUsers = selectedUsers,
                    searchQuery = searchQuery,
                    onUserSelect = { user ->
                        selectedUsers = if (user in selectedUsers) {
                            selectedUsers - user
                        } else {
                            selectedUsers + user
                        }
                    })
            }

            else -> {}
        }


    }
}

//상단 타이틀
@Composable
fun TopSection(
    selectedUsers: List<User>, onNavigateToBack: () -> Unit, onCreateChatRoom: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
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
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold
        )

        IconButton(enabled = selectedUsers.isNotEmpty(), onClick = { onCreateChatRoom() }) {
            Icon(Icons.Default.Check, contentDescription = "Confirm")
        }
    }
}

@Composable
private fun FriendSearchSection(
    modifier: Modifier = Modifier,
    searchQuery: String,
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
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray,
            )
        },
        trailingIcon = {
            // 초기화 버튼
            if (searchQuery.isEmpty()) return@TextField
            IconButton(onClick = {
                onSearchQueryChange("")
            }) {
                Icon(Icons.Default.Cancel, contentDescription = null)
            }
        },
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent
        ),
        textStyle = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurface),
    )
}