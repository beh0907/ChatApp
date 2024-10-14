package com.skymilk.chatapp.store.presentation.screen.main.chatRoom.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.skymilk.chatapp.store.domain.model.ChatMessage
import com.skymilk.chatapp.store.domain.model.ChatRoomWithUsers
import com.skymilk.chatapp.store.domain.model.MessageType
import com.skymilk.chatapp.store.domain.model.User
import com.skymilk.chatapp.store.presentation.common.ScrollToEndCallback
import com.skymilk.chatapp.store.presentation.screen.main.chatRoom.state.ImageUploadState
import com.skymilk.chatapp.ui.theme.SamsungOneFont
import com.skymilk.chatapp.utils.DateUtil
import kotlinx.coroutines.launch
import kotlin.random.Random

@Composable
fun ChatMessageList(
    modifier: Modifier = Modifier,
    chatRoom: ChatRoomWithUsers,
    chatMessages: List<ChatMessage>,
    currentUser: User,
    uploadState: ImageUploadState,
    onNavigateToProfile: (User) -> Unit,
    onNavigateToImageViewer: (String) -> Unit
) {
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    var previousMessagesSize by remember { mutableIntStateOf(chatMessages.size) }
    var showNewMessage by remember { mutableStateOf("") }
    var showNewMessageNotification by remember { mutableStateOf(false) }

    //메시지가 새로 들어올때마다 이벤트 처리
    LaunchedEffect(chatMessages.size) {
        if (chatMessages.size > previousMessagesSize) {
            val newMessage = chatMessages.first()

            if (newMessage.senderId == currentUser.id) {
                // 새 메시지가 사용자가 작성한 메시지인 경우
                listState.scrollToItem(0)
            } else {
                // 새 메시지가 다른 사용자가 작성한 메시지인 경우
                if (listState.firstVisibleItemIndex < 2) {
                    // 스크롤이 최하단에 있으면 자동으로 스크롤
                    listState.scrollToItem(0)
                } else {
                    // 스크롤이 최하단에 있지 않으면 알림 표시
                    showNewMessageNotification = true

                    showNewMessage = when (newMessage.type) {
                        MessageType.TEXT -> newMessage.content
                        MessageType.IMAGE -> "이미지"
                        MessageType.VIDEO -> "비디오"
                        MessageType.SYSTEM -> "시스템 메시지"
                    }
                }
            }
        }
        previousMessagesSize = chatMessages.size
    }

    //스크롤이 최하단에 위치할 경우
    ScrollToEndCallback(listState, isReverse = true) {
        //최신 메시지 알림 영역을 제거한다
        showNewMessage = ""
        showNewMessageNotification = false
    }

    Box(modifier = modifier) {
        LazyColumn(
            modifier = modifier,
            reverseLayout = true, //반대로
            state = listState,
            verticalArrangement = Arrangement.Top,
        ) {
            //내가 전송중인 이미지 정보
            //이미지가 전송중일때만 정보를 표시
            if (uploadState is ImageUploadState.Progress) {
                item {
                    MyUploadImageItem(uploadState)
                }

                if (chatMessages.isNotEmpty())
                    scope.launch { listState.scrollToItem(0) }

            }

            //메시지 이력 목록
            items(
                count = chatMessages.size,
                key = { index -> chatMessages[index].id }
            ) { index ->
                val chatMessage = chatMessages[index]

                when(chatMessage.type) {
                    //시스템 메시지는 별도 표시
                    MessageType.SYSTEM -> {
                        SystemMessageItem(
                            content = chatMessage.content
                        )
                    }

                    //채팅 메시지 표시
                    else -> {
                        //내가 작성한 메시지
                        if (chatMessage.senderId == currentUser.id) {
                            MyMessageItem(
                                chatMessage = chatMessage,
                                onNavigateToImageViewer = onNavigateToImageViewer
                            )

                        } else {
                            // message.senderId와 일치하는 chatRoom.participants 내 user를 찾음
                            val sender = chatRoom.participants.find { it.id == chatMessage.senderId }
                                ?: User()

                            //다른 사람이 작성한 메시지
                            OtherMessageItem(
                                chatMessage = chatMessage,
                                sender = sender,
                                onNavigateToProfile = onNavigateToProfile,
                                onNavigateToImageViewer = onNavigateToImageViewer
                            )
                        }
                    }
                }

                //첫번째 메시지거나 이전 메시지와 날짜가 다르다면
                // 날짜 정보 표시
                if (index == chatMessages.size - 1 || !DateUtil.isToday(
                        chatMessages[index + 1].timestamp,
                        chatMessage.timestamp
                    )
                ) {
                    //중앙 날짜 정보
                    FullDateItem(DateUtil.getFullDate(chatMessage.timestamp))
                }
            }
        }


        // 새 메시지 알림
        if (showNewMessageNotification) {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 10.dp)
                    .clip(
                        RoundedCornerShape(10.dp)
                    )
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f))
                    .clickable {
                        scope.launch {
                            listState.scrollToItem(0)
                            showNewMessageNotification = false
                        }
                    }
                    .padding(5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = showNewMessage,
                    fontFamily = SamsungOneFont,
                    color = MaterialTheme.colorScheme.surface
                )

                Icon(
                    imageVector = Icons.Rounded.KeyboardArrowDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.surface
                )
            }
        }
    }
}

@Composable
fun ChatMessageListShimmer(
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier
    ) {
        items(20) {
            // 랜덤으로 아이템 선택
            val isMyMessage = Random.nextBoolean()

            if (isMyMessage) MyMessageItemShimmer() // 사용자 메시지 로딩 아이템
            else OtherMessageItemShimmer() // 상대방 메시지 로딩 아이템

        }
    }
}