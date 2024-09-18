package com.skymilk.chatapp.store.presentation.screen.main.chatRoom

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.skymilk.chatapp.store.domain.model.ChatRoomWithUsers
import com.skymilk.chatapp.store.domain.model.User
import com.skymilk.chatapp.ui.theme.Black
import com.skymilk.chatapp.ui.theme.HannaPro

@Composable
fun ChatRoomScreen(
    modifier: Modifier = Modifier,
    viewModel: ChatRoomViewModel,
    chatRoom: ChatRoomWithUsers,
    currentUser: User,
    onNavigateToBack: () -> Unit
) {
    val chatMessages by viewModel.chatMessages.collectAsStateWithLifecycle()

    Column(
        modifier = modifier.fillMaxSize()
    ) {

        TopSection(onNavigateToBack)


        // ChatRoomMessages 영역
        ChatRoomMessages(
            modifier = Modifier
                .weight(1f) // 키보드가 올라오면 이 영역이 줄어듬
                .fillMaxWidth(),
            chatRoom = chatRoom,
            chatMessages = chatMessages,
            currentUser = currentUser
        )

        BottomSection(
            modifier = Modifier.imePadding(),
            onSendMessage = viewModel::sendMessage,
            onSendImageMessage = viewModel::sendImageMessage,
            currentUser = currentUser
        )
    }

}


//상단 타이틀
@Composable
fun TopSection(onNavigateToBack: () -> Unit) {
    val uiColor = if (isSystemInDarkTheme()) Color.White else Black

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = {
            onNavigateToBack()
        }) {
            Icon(
                imageVector = Icons.AutoMirrored.Default.ArrowBack,
                contentDescription = null,
                tint = uiColor
            )
        }

        Text(
            modifier = Modifier.weight(1f),
            text = "채팅",
            fontFamily = HannaPro,
            style = MaterialTheme.typography.titleLarge,
            color = uiColor
        )
    }
}


//하단 텍스트 입력
@Composable
fun BottomSection(
    modifier: Modifier = Modifier,
    onSendMessage: (String, String) -> Unit,
    onSendImageMessage: (String, String) -> Unit,
    currentUser: User
) {
    Row(modifier = modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        val keyboardController = LocalSoftwareKeyboardController.current
        val uiColor = if (isSystemInDarkTheme()) Color.White else Black
        var message by remember { mutableStateOf("") }

        IconButton(onClick = {

        }) {
            Icon(
                modifier = Modifier.size(36.dp),
                imageVector = Icons.Default.AttachFile, contentDescription = null, tint = uiColor
            )
        }

        OutlinedTextField(
            modifier = Modifier.weight(1f),
            singleLine = true,
            value = message,
            onValueChange = { message = it },
            label = {
                Text(
                    text = "채팅을 입력해주세요.",
                    style = MaterialTheme.typography.labelMedium,
                    color = uiColor,
                )
            },
            keyboardActions = KeyboardActions(onDone = {
                //메시지 전송
                onSendMessage(currentUser.id, message)

                //텍스트 필드 초기화
                message = ""

                //키보드 숨기기
                keyboardController?.hide()
            })
        )

        IconButton(
            onClick = {
                //메시지 전송
                onSendMessage(currentUser.id, message)

                //텍스트 필드 초기화
                message = ""

                //키보드 숨기기
                keyboardController?.hide()
            },
            enabled = message.isNotBlank()
        ) {
            Icon(
                modifier = Modifier.size(36.dp),
                imageVector = Icons.AutoMirrored.Default.Send,
                contentDescription = null,
                tint = uiColor
            )
        }
    }
}