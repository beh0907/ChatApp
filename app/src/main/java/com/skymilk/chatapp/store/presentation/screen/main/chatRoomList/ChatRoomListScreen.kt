package com.skymilk.chatapp.store.presentation.screen.main.chatRoomList

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.skymilk.chatapp.R
import com.skymilk.chatapp.store.domain.model.User
import com.skymilk.chatapp.store.presentation.common.EmptyScreen
import com.skymilk.chatapp.store.presentation.common.ErrorScreen
import com.skymilk.chatapp.store.presentation.screen.main.chatRoomList.component.ChatRoomList
import com.skymilk.chatapp.store.presentation.screen.main.chatRoomList.component.ChatRoomListShimmer
import com.skymilk.chatapp.ui.theme.LeeSeoYunFont
import com.skymilk.chatapp.ui.theme.dimens

@Composable
fun ChatListScreen(
    modifier: Modifier = Modifier,
    viewModel: ChatRoomListViewModel,
    currentUser: User,
    onNavigateToChatRoom: (String) -> Unit,
    onNavigateToChatRoomCreate: () -> Unit
) {
    val chatListState by viewModel.chatRoomsState.collectAsStateWithLifecycle()

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        TopSection(
            onNavigateToChatRoomCreate = onNavigateToChatRoomCreate
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

                ChatRoomList(chatRooms, currentUser, onNavigateToChatRoom)
            }

            is ChatRoomsState.Error -> {
                // 에러 발생 시 표시
                val message = (chatListState as ChatRoomsState.Error).message

                ErrorScreen(message = message, retry = viewModel::loadChatRooms)
            }
        }
    }
}

//상단 타이틀
@Composable
fun TopSection(onNavigateToChatRoomCreate: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = "채팅",
            fontFamily = LeeSeoYunFont,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface
        )

        IconButton(onClick = { onNavigateToChatRoomCreate() }) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_chat_add_on),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }

    HorizontalDivider()
}

@Composable
fun CreateChatRoomDialog(
    onCreateChatRoom: (String) -> Unit,
    currentUser: User
) {
    var chatRoomName by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "채팅방 생성")

        Spacer(modifier = Modifier.height(MaterialTheme.dimens.small3))

        TextField(
            value = chatRoomName,
            onValueChange = { chatRoomName = it },
            label = {
                Text("채팅방 이름")
            }
        )

        Spacer(modifier = Modifier.height(MaterialTheme.dimens.small3))

        Button(onClick = { onCreateChatRoom("") }, modifier = Modifier.fillMaxWidth()) {
            Text(text = "생성")
        }
    }
}
