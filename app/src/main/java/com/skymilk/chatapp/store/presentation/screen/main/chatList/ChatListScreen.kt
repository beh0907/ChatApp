package com.skymilk.chatapp.store.presentation.screen.main.chatList

import android.util.Log
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.AddComment
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.skymilk.chatapp.store.domain.model.ChatRoomWithUsers
import com.skymilk.chatapp.store.domain.model.User
import com.skymilk.chatapp.ui.theme.Black
import com.skymilk.chatapp.ui.theme.HannaPro
import com.skymilk.chatapp.ui.theme.dimens

@Composable
fun ChatListScreen(
    modifier: Modifier,
    viewModel: ChatListViewModel,
    currentUser: User,
    onChatItemClick: (ChatRoomWithUsers) -> Unit
) {
    val chatRooms by viewModel.chatRooms.collectAsStateWithLifecycle()

    Log.d("chatRooms", chatRooms.toString())

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        TopSection()

        HorizontalDivider()

        ChatRoomList(chatRooms, currentUser, onChatItemClick)
    }
}

//상단 타이틀
@Composable
fun TopSection() {
    val uiColor = if (isSystemInDarkTheme()) Color.White else Black

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = "채팅",
            fontFamily = HannaPro,
            style = MaterialTheme.typography.titleLarge,
            color = uiColor
        )

        IconButton(onClick = {

        }) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                tint = uiColor
            )
        }
    }
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
