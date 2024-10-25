package com.skymilk.chatapp.store.presentation.screen.main.chatRoomList

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MapsUgc
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.skymilk.chatapp.store.domain.model.User
import com.skymilk.chatapp.store.presentation.common.EmptyScreen
import com.skymilk.chatapp.store.presentation.common.ErrorScreen
import com.skymilk.chatapp.store.presentation.screen.main.chatRoomList.component.ChatRoomList
import com.skymilk.chatapp.store.presentation.screen.main.chatRoomList.component.ChatRoomListShimmer

@Composable
fun ChatRoomsScreen(
    modifier: Modifier = Modifier,
    viewModel: ChatRoomsViewModel,
    onEvent: (ChatRoomsEvent) -> Unit,
    currentUser: User,
    onNavigateToChatRoom: (String) -> Unit,
    onNavigateToChatRoomInvite: () -> Unit
) {
    val chatListState by viewModel.chatRoomsState.collectAsStateWithLifecycle()
    val chatRoomAlarmsDisabled by viewModel.chatRoomAlarmsDisabled.collectAsStateWithLifecycle()

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        TopSection(
            onNavigateToChatRoomInvite = onNavigateToChatRoomInvite
        )

        when (chatListState) {
            is ChatRoomsState.Initial -> {}

            is ChatRoomsState.Loading -> {
                // 로딩 중 UI 표시
                ChatRoomListShimmer()
            }

            is ChatRoomsState.Success -> {
                //로딩이 완료 됐을 때 표시
                val chatRooms = (chatListState as ChatRoomsState.Success).chatRooms

                if (chatRooms.isEmpty()) {
                    EmptyScreen("등록된 채팅방이 없습니다.")
                    return
                }

                ChatRoomList(
                    chatRooms = chatRooms,
                    currentUser = currentUser,
                    chatRoomAlarmsDisabled = chatRoomAlarmsDisabled,
                    onNavigateToChatRoom = onNavigateToChatRoom
                )
            }

            is ChatRoomsState.Error -> {
                // 에러 발생 시 표시
                val message = (chatListState as ChatRoomsState.Error).message

                ErrorScreen(message = message, retry = { onEvent(ChatRoomsEvent.LoadChatRooms) })
            }
        }
    }
}

//상단 타이틀
@Composable
fun TopSection(onNavigateToChatRoomInvite: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = "채팅",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold
        )

        IconButton(onClick = { onNavigateToChatRoomInvite() }) {
            Icon(
                imageVector = Icons.Rounded.MapsUgc,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }

    HorizontalDivider()
}