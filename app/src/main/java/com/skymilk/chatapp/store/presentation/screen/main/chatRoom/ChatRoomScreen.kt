package com.skymilk.chatapp.store.presentation.screen.main.chatRoom

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.NotificationsOff
import androidx.compose.material.icons.rounded.Add
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
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.skymilk.chatapp.store.domain.model.ChatRoomWithUsers
import com.skymilk.chatapp.store.domain.model.User
import com.skymilk.chatapp.store.presentation.common.CustomConfirmDialog
import com.skymilk.chatapp.store.presentation.common.CustomProgressDialog
import com.skymilk.chatapp.store.presentation.common.EmptyScreen
import com.skymilk.chatapp.store.presentation.common.ErrorScreen
import com.skymilk.chatapp.store.presentation.screen.main.chatRoom.state.ChatMessagesState
import com.skymilk.chatapp.store.presentation.screen.main.chatRoom.state.ChatRoomState
import com.skymilk.chatapp.ui.theme.LeeSeoYunFont
import gun0912.tedimagepicker.builder.TedImagePicker

@Composable
fun ChatRoomScreen(
    modifier: Modifier = Modifier,
    viewModel: ChatRoomViewModel,
    currentUser: User,
    onNavigateToBack: () -> Unit,
    onNavigateToProfile: (User) -> Unit,
    onNavigateToImageViewer: (String) -> Unit
) {
    val chatRoomState by viewModel.chatRoomState.collectAsStateWithLifecycle()
    val chatMessagesState by viewModel.chatMessagesState.collectAsStateWithLifecycle()
    val uploadState by viewModel.uploadState.collectAsStateWithLifecycle()
    val alarmState by viewModel.alarmState.collectAsStateWithLifecycle()

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
                    chatRoom = chatRoom,
                    currentUser = currentUser,
                    alarmState = alarmState,
                    onNavigateToBack = onNavigateToBack,
                    toggleAlarmState = viewModel::toggleAlarmState
                )

                //채팅 목록 영역
                when (chatMessagesState) {
                    is ChatMessagesState.Initial -> {}

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
                        val chatMessages = (chatMessagesState as ChatMessagesState.Success).chatMessages

                        if (chatMessages.isEmpty()) {
                            EmptyScreen("채팅을 입력해주세요.")
                            return
                        }

                        ChatMessageList(
                            modifier = Modifier
                                .weight(1f) // 키보드가 올라오면 이 영역이 줄어듬
                                .fillMaxWidth(),
                            chatRoom = chatRoom,
                            chatMessages = chatMessages,
                            currentUser = currentUser,
                            uploadState = uploadState,
                            onNavigateToProfile = onNavigateToProfile,
                            onNavigateToImageViewer = onNavigateToImageViewer
                        )
                    }

                    is ChatMessagesState.Error -> {
                        ErrorScreen(
                            message = "메시지를 불러오지 못했습니다.",
                            retry = viewModel::loadChatMessages
                        )
                    }
                }


                //채팅 입력 영역
                BottomSection(
                    modifier = Modifier.imePadding(),
                    onSendMessage = { sender, content ->
                        viewModel.sendMessage(sender, content, chatRoom.participants)
                    },
                    onSendImageMessage = { sender, uri ->
                        viewModel.sendImageMessage(sender, uri, chatRoom.participants)
                    },
                    user = currentUser,
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
fun TopSection(
    chatRoom: ChatRoomWithUsers,
    currentUser: User,
    alarmState: Boolean,
    onNavigateToBack: () -> Unit,
    toggleAlarmState: () -> Unit
) {
    val title = when (chatRoom.participants.size) {
        1 -> chatRoom.participants.first().username
        2 -> chatRoom.participants.find { it.id != currentUser.id }?.username ?: ""
        else -> "그룹채팅"
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
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
            text = title,
            fontFamily = LeeSeoYunFont,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface
        )


        //혼자 있는 방은 알람 자체가 발생하지 않기 때문에 설정하지 않는다
        if (chatRoom.participants.size != 1) {
            IconButton(onClick = {
                toggleAlarmState()
            }) {
                Icon(
                    imageVector = if (alarmState) Icons.Outlined.NotificationsOff else Icons.Outlined.Notifications,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}


//하단 텍스트 입력
@Composable
fun BottomSection(
    modifier: Modifier = Modifier,
    onSendMessage: (User, String) -> Unit,
    onSendImageMessage: (User, Uri) -> Unit,
    user: User,
) {
    var message by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current
    val context = LocalContext.current

    val constraints = ConstraintSet {
        val messageTextField = createRefFor("messageTextField")
        val attachIcon = createRefFor("attachIcon")
        val sendIcon = createRefFor("sendIcon")

        constrain(messageTextField) {
            top.linkTo(parent.top)
            bottom.linkTo(parent.bottom)
            start.linkTo(attachIcon.end)
            end.linkTo(sendIcon.start)
            width = Dimension.fillToConstraints
            height = Dimension.wrapContent
        }
        constrain(attachIcon) {
            top.linkTo(messageTextField.top)
            bottom.linkTo(messageTextField.bottom)
            start.linkTo(parent.start)
            height = Dimension.fillToConstraints
        }
        constrain(sendIcon) {
            top.linkTo(messageTextField.top)
            bottom.linkTo(messageTextField.bottom)
            end.linkTo(parent.end)
            height = Dimension.fillToConstraints
        }
    }

    ConstraintLayout(
        constraintSet = constraints,
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer)
    ) {
        //이미지 첨부 버튼
        Box(
            modifier = Modifier
                .layoutId("attachIcon")
                .clickable {
                    TedImagePicker
                        .with(context)
                        .start { uri ->
                            onSendImageMessage(user, uri)
                        }
                },
            contentAlignment = Alignment.BottomCenter
        ) {
            Icon(
                modifier = Modifier
                    .padding(start = 10.dp, end = 10.dp, bottom = 16.dp)
                    .size(24.dp),
                imageVector = Icons.Rounded.Add,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface
            )
        }

        //채팅 입력
        OutlinedTextField(
            modifier = Modifier.layoutId("messageTextField"),
            maxLines = 5,
            value = message,
            onValueChange = { message = it },
            placeholder = {
                Text(
                    text = "채팅을 입력해주세요.",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.Gray,
                    fontFamily = LeeSeoYunFont
                )
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

        //메시지 전송 버튼
        if (message.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .layoutId("sendIcon")
                    .background(MaterialTheme.colorScheme.inversePrimary)
                    .clickable {
                        //메시지 전송
                        onSendMessage(user, message)

                        //메시지 초기화
                        message = ""

                        //키보드 숨기기
                        keyboardController?.hide()
                    },
                contentAlignment = Alignment.BottomCenter
            ) {
                Icon(
                    modifier = Modifier
                        .padding(start = 10.dp, end = 10.dp, bottom = 16.dp)
                        .size(24.dp),
                    imageVector = Icons.AutoMirrored.Rounded.Send,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}