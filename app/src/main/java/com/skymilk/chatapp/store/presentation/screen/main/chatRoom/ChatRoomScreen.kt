package com.skymilk.chatapp.store.presentation.screen.main.chatRoom

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.splineBasedDecay
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.NotificationsOff
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.skymilk.chatapp.store.domain.model.ChatRoomWithUsers
import com.skymilk.chatapp.store.domain.model.User
import com.skymilk.chatapp.store.presentation.common.CustomAlertDialog
import com.skymilk.chatapp.store.presentation.common.CustomConfirmDialog
import com.skymilk.chatapp.store.presentation.common.CustomProgressDialog
import com.skymilk.chatapp.store.presentation.common.ErrorScreen
import com.skymilk.chatapp.store.presentation.screen.main.chatRoom.components.ChatMessageList
import com.skymilk.chatapp.store.presentation.screen.main.chatRoom.components.ChatMessageListShimmer
import com.skymilk.chatapp.store.presentation.screen.main.chatRoom.components.ParticipantList
import com.skymilk.chatapp.store.presentation.screen.main.chatRoom.state.ChatMessagesState
import com.skymilk.chatapp.store.presentation.screen.main.chatRoom.state.ChatRoomState
import gun0912.tedimagepicker.builder.TedImagePicker
import kotlin.math.roundToInt

@Composable
fun ChatRoomScreen(
    modifier: Modifier = Modifier,
    viewModel: ChatRoomViewModel,
    currentUser: User,
    onNavigateToBack: () -> Unit,
    onNavigateToProfile: (User) -> Unit,
    onNavigateToImageViewer: (String) -> Unit,
    onNavigateToInviteFriends: (String, List<String>) -> Unit,
) {
    val chatRoomState by viewModel.chatRoomState.collectAsStateWithLifecycle()
    val chatMessagesState by viewModel.chatMessagesState.collectAsStateWithLifecycle()
    val uploadState by viewModel.uploadState.collectAsStateWithLifecycle()
    val alarmState by viewModel.alarmState.collectAsStateWithLifecycle()

    var visibleExitDialog by remember { mutableStateOf(false) }
    var visibleDrawer by remember { mutableStateOf(false) }

    // 드로어가 오픈되어 있을경우 백버튼을 누르면 드로어만 닫는다
    BackHandler(enabled = visibleDrawer) {
        visibleDrawer = false
    }

    Box(
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

                Column(
                    modifier = modifier.fillMaxSize()
                ) {
                    //제목 영역
                    TopSection(chatRoom = chatRoom,
                        currentUser = currentUser,
                        onNavigateToBack = onNavigateToBack,
                        onOpenDrawer = { visibleDrawer = true })

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
                            val chatMessages =
                                (chatMessagesState as ChatMessagesState.Success).chatMessages

                            //채팅 메시지 목록
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
                                message = "메시지를 불러오지 못했습니다.", retry = viewModel::loadChatMessages
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

                //우측 드로어 메뉴
                CustomRightSideDrawer(drawerVisibility = visibleDrawer,
                    currentUser = currentUser,
                    chatRoom = chatRoom,
                    alarmState = alarmState,
                    onVisibleExitDialog = { visibleExitDialog = true },
                    onToggleAlarmState = viewModel::toggleAlarmState,
                    onNavigateToProfile = onNavigateToProfile,
                    onNavigateToInviteFriends = {
                        //채팅방 아이디, 현재 참여자 목록
                        onNavigateToInviteFriends(chatRoom.id, chatRoom.participants.map { it.id })
                    },
                    onCloseDrawer = {
                        visibleDrawer = false
                    })

            }

            is ChatRoomState.Error -> {
                //채팅방 불러오기 실패
                CustomConfirmDialog("채팅방을 불러오지 못했습니다.", onNavigateToBack)
            }
        }

        if (visibleExitDialog)
            CustomAlertDialog(
                message = "채팅방을 나가시겠습니까?",
                onConfirm = {
                    viewModel.exitChatRoom(currentUser, onNavigateToBack)
                },
                onDismiss = { visibleExitDialog = false }
            )
    }
}


//상단 타이틀
@Composable
fun TopSection(
    chatRoom: ChatRoomWithUsers,
    currentUser: User,
    onNavigateToBack: () -> Unit,
    onOpenDrawer: () -> Unit
) {
    val title = when (chatRoom.participants.size) {
        1 -> chatRoom.participants.first().username
        2 -> chatRoom.participants.find { it.id != currentUser.id }?.username ?: ""
        else -> "그룹채팅 ${chatRoom.participants.size}"
    }

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

        //타이틀
        Text(
            modifier = Modifier.weight(1f),
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold
        )

        //드로어 메뉴 오픈
        IconButton(onClick = {
            onOpenDrawer()
        }) {
            Icon(
                imageVector = Icons.Outlined.Menu,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface
            )
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
                }, contentAlignment = Alignment.BottomCenter
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
                )
            },
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent
            ),
            textStyle = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurface)
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
                    }, contentAlignment = Alignment.BottomCenter
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


//우 -> 좌 방향 커스텀 드로어
//기본은 좌 -> 우이기 때문에 별도 구현
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BoxScope.CustomRightSideDrawer(
    drawerVisibility: Boolean,
    currentUser: User,
    chatRoom: ChatRoomWithUsers,
    alarmState: Boolean,
    onVisibleExitDialog: () -> Unit,
    onToggleAlarmState: () -> Unit,
    onNavigateToProfile: (User) -> Unit,
    onNavigateToInviteFriends: () -> Unit,
    onCloseDrawer: () -> Unit,
) {
    val ratio = 0.8f
    val density = LocalDensity.current
    val drawerWidth = with(density) { (LocalConfiguration.current.screenWidthDp * ratio).dp.toPx() }

    val anchors = remember {
        DraggableAnchors {
            DrawerValue.Closed at drawerWidth
            DrawerValue.Open at 0f
        }
    }

    val draggableState = remember {
        AnchoredDraggableState(initialValue = if (drawerVisibility) DrawerValue.Open else DrawerValue.Closed,
            anchors = anchors,
            positionalThreshold = { distance: Float -> distance * 0.5f },
            velocityThreshold = { with(density) { 100.dp.toPx() } },
            snapAnimationSpec = SpringSpec(stiffness = Spring.StiffnessMediumLow),
            decayAnimationSpec = splineBasedDecay(density),
            confirmValueChange = { true })
    }

    //드로어 상태 애니메이션 표현
    LaunchedEffect(drawerVisibility) {
        val value = if (drawerVisibility) DrawerValue.Open else DrawerValue.Closed
        draggableState.animateTo(value)
    }

    //드래그로 닫힌다면 onCloseDrawer 호출
    LaunchedEffect(draggableState.currentValue) {
        if (draggableState.currentValue == DrawerValue.Closed) {
            onCloseDrawer()
        }
    }

    // 드로어 뒷배경
    AnimatedVisibility(
        visible = drawerVisibility, enter = fadeIn(), exit = fadeOut()
    ) {
        Box(modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable(
                //리플 효과 제거
                indication = null,
                interactionSource = remember { MutableInteractionSource() }) { onCloseDrawer() })
    }

    // 드로어 레이아웃
    AnimatedVisibility(
        visible = drawerVisibility,
        enter = slideInHorizontally(initialOffsetX = { it }),
        exit = slideOutHorizontally(targetOffsetX = { it }),
        modifier = Modifier.align(Alignment.CenterEnd)
    ) {
        Box(modifier = Modifier
            .offset {
                IntOffset(
                    x = draggableState.offset.roundToInt(), y = 0
                )
            }
            .fillMaxHeight()
            .fillMaxWidth(ratio)
            .clip(RoundedCornerShape(topStart = 10.dp, bottomStart = 10.dp))
            .background(MaterialTheme.colorScheme.surface)
            .anchoredDraggable(
                state = draggableState, orientation = Orientation.Horizontal
            )) {
            Column {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    text = "채팅방 정보",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                HorizontalDivider(modifier = Modifier.padding(bottom = 10.dp))

                //채팅방 참여자 목록
                ParticipantList(
                    modifier = Modifier.weight(1f),
                    currentUser = currentUser,
                    participants = chatRoom.participants,
                    onUserItemClick = onNavigateToProfile,
                    onNavigateToInviteFriends = onNavigateToInviteFriends
                )

                HorizontalDivider()

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = { onVisibleExitDialog() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.Logout,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }


                    //혼자 있는 방은 알람 자체가 발생하지 않기 때문에 설정하지 않는다
                    if (chatRoom.participants.size != 1) {
                        IconButton(onClick = {
                            onToggleAlarmState()
                        }) {
                            Icon(
                                imageVector = if (alarmState) Icons.Outlined.NotificationsOff else Icons.Filled.Notifications,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}