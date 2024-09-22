package com.skymilk.chatapp.store.presentation.screen.main.chatRoom

import android.net.Uri
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.skymilk.chatapp.store.domain.model.ChatRoomWithUsers
import com.skymilk.chatapp.store.domain.model.User
import com.skymilk.chatapp.store.presentation.common.CustomConfirmDialog
import com.skymilk.chatapp.store.presentation.common.CustomProgressDialog
import com.skymilk.chatapp.store.presentation.screen.main.chatRoom.components.ChatMessageList
import com.skymilk.chatapp.store.presentation.screen.main.chatRoom.components.ChatMessageListShimmer
import com.skymilk.chatapp.store.presentation.screen.main.chatRoom.state.ChatMessagesState
import com.skymilk.chatapp.store.presentation.screen.main.chatRoom.state.ChatRoomState
import com.skymilk.chatapp.ui.theme.Black
import com.skymilk.chatapp.ui.theme.HannaPro
import gun0912.tedimagepicker.builder.TedImagePicker

@Composable
fun ChatRoomScreen(
    modifier: Modifier = Modifier,
    viewModel: ChatRoomViewModel,
    currentUser: User,
    onNavigateToBack: () -> Unit
) {
    val chatRoomState by viewModel.chatRoomState.collectAsStateWithLifecycle()
    val chatMessagesState by viewModel.chatMessagesState.collectAsStateWithLifecycle()
    val uploadState by viewModel.uploadState.collectAsStateWithLifecycle()

    Column(
        modifier = modifier.fillMaxSize()
    ) {

        //채팅방 정보 체크
        when (chatRoomState) {
            is ChatRoomState.Initial -> {}
            is ChatRoomState.Loading -> {
                //로딩 알림
                CustomProgressDialog("채팅방을 불러오고 있습니다.")
            }

            is ChatRoomState.Success -> {
                //채팅방 로드 성공
                val chatRoom = (chatRoomState as ChatRoomState.Success).chatRoom

                //제목 영역
                TopSection(
                    onNavigateToBack = onNavigateToBack,
                    chatRoom = chatRoom,
                    currentUser = currentUser
                )

                //채팅 목록 영역
                when (chatMessagesState) {
                    is ChatMessagesState.Loading -> {
                        // 로딩 중 UI 표시
                        ChatMessageListShimmer(
                            modifier = Modifier
                                .weight(1f) // 키보드가 올라오면 이 영역이 줄어듬
                                .fillMaxWidth()
                        )
                    }

                    is ChatMessagesState.Success -> {
                        //로딩이 완료 됐을 때 표시
                        val chatMessages =
                            (chatMessagesState as ChatMessagesState.Success).chatMessages

                        ChatMessageList(
                            modifier = Modifier
                                .weight(1f) // 키보드가 올라오면 이 영역이 줄어듬
                                .fillMaxWidth(),
                            chatRoom = chatRoom,
                            chatChatMessages = chatMessages,
                            currentUser = currentUser,
                            uploadState = uploadState
                        )
                    }

                    else -> Unit
                }


                //채팅 입력 영역
                BottomSection(
                    modifier = Modifier,
                    onSendMessage = viewModel::sendMessage,
                    onSendImageMessage = viewModel::sendImageMessage,
                    userId = currentUser.id,
                )
            }

            is ChatRoomState.Error -> {
                //채팅방 불러오기 실패
                CustomConfirmDialog("채팅방을 불러오지 못했습니다.", onNavigateToBack)
            }
        }
    }

}


//상단 타이틀
@Composable
fun TopSection(onNavigateToBack: () -> Unit, chatRoom: ChatRoomWithUsers, currentUser: User) {
    val uiColor = if (isSystemInDarkTheme()) Color.White else Black

    val title = when (chatRoom.participants.size) {
        1 -> chatRoom.participants.first().username
        2 -> chatRoom.participants.find { it.id != currentUser.id }?.username ?: ""
        else -> "그룹채팅"
    }

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
            text = title,
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
    onSendImageMessage: (String, Uri) -> Unit,
    userId: String,
) {
    var message by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current

    val context = LocalContext.current

    OutlinedTextField(
        modifier = modifier
            .fillMaxWidth(),
        maxLines = 5,
        value = message,
        onValueChange = { message = it },
        placeholder = {
            Text(
                text = "채팅을 입력해주세요.",
                style = MaterialTheme.typography.labelMedium,
                color = Color.Gray,
                fontFamily = HannaPro
            )
        },
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            focusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            unfocusedContainerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        textStyle = TextStyle(
            fontFamily = HannaPro,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            color = Black
        ),
        leadingIcon = {
            IconButton(
                onClick = {
                    //테드 이미지 픽커
                    TedImagePicker.with(context)
                        .start { uri ->
                            onSendImageMessage(userId, uri)
                        }
                },
                modifier = Modifier
                    .size(36.dp)
            ) {
                Icon(
                    modifier = Modifier.size(36.dp),
                    imageVector = Icons.Default.AttachFile,
                    contentDescription = null,
                    tint = Black
                )
            }
        },
        trailingIcon = {
            if (message.isNotBlank()) {
                IconButton(
                    onClick = {
                        // 메시지 전송
                        onSendMessage(userId, message)

                        // 텍스트 필드 초기화
                        message = ""

                        // 키보드 숨기기
                        keyboardController?.hide()
                    }
                ) {
                    Icon(
                        modifier = Modifier.size(36.dp),
                        imageVector = Icons.AutoMirrored.Default.Send,
                        contentDescription = null,
                        tint = Black
                    )
                }
            }
        }
    )
}