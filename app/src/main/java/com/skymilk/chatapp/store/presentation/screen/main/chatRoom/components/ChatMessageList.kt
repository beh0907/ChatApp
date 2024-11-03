package com.skymilk.chatapp.store.presentation.screen.main.chatRoom.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.skymilk.chatapp.store.data.dto.ChatMessage
import com.skymilk.chatapp.store.data.dto.MessageType
import com.skymilk.chatapp.store.data.dto.ParticipantStatus
import com.skymilk.chatapp.store.data.dto.User
import com.skymilk.chatapp.store.domain.model.ChatRoomWithParticipants
import com.skymilk.chatapp.store.presentation.common.ScrollToEndCallback
import com.skymilk.chatapp.store.presentation.screen.main.chatRoom.state.ImageUploadState
import com.skymilk.chatapp.store.presentation.utils.DateUtil
import com.skymilk.chatapp.ui.theme.isAppInDarkTheme
import kotlinx.coroutines.launch
import kotlin.random.Random

@Composable
fun BoxScope.ChatMessageList(
    modifier: Modifier = Modifier,
    chatRoom: ChatRoomWithParticipants,
    participantsStatus: List<ParticipantStatus>,
    chatMessages: List<ChatMessage>,
    currentUser: User,
    uploadState: ImageUploadState,
    onNavigateToProfile: (User) -> Unit,
    onNavigateToImagePager: (List<String>, Int, String, Long) -> Unit
) {
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current

    var previousMessagesSize by remember { mutableIntStateOf(chatMessages.size) }
    var showNewMessage by remember { mutableStateOf("") }
    var showNewMessageUserName by remember { mutableStateOf("") }
    var showNewMessageNotification by remember { mutableStateOf(false) }

    //메시지가 새로 들어올때마다 이벤트 처리
    LaunchedEffect(chatMessages.size) {
        if (chatMessages.size > previousMessagesSize) {
            val newMessage = chatMessages.first()

            //시스템 메시지는 표시하지 않는다
            if (newMessage.messageContents.first().type == MessageType.SYSTEM) return@LaunchedEffect

            if (newMessage.senderId == currentUser.id) {
                // 새 메시지가 사용자가 작성한 메시지인 경우
                listState.scrollToItem(0)
            } else {
                // 새 메시지가 다른 사용자가 작성한 메시지인 경우
                if (listState.firstVisibleItemIndex < (chatMessages.size - previousMessagesSize) + 2) {
                    // 스크롤이 최하단에 있으면 자동으로 스크롤
                    listState.scrollToItem(0)
                } else {
                    // 스크롤이 최하단에 있지 않으면 알림 표시
                    showNewMessageNotification = true

                    //유저 이름 저장
                    showNewMessageUserName =
                        chatRoom.participants.find { it.id == newMessage.senderId }?.username
                            ?: "알 수 없음"

                    //메시지 저장
                    showNewMessage = when (newMessage.messageContents.first().type) {
                        MessageType.TEXT -> newMessage.messageContents.first().content
                        MessageType.IMAGE -> "이미지"
                        MessageType.VIDEO -> "비디오"
                        MessageType.SYSTEM -> "시스템 메시지"
                    }
                }
            }

            //채팅 메시지 사이즈 저장
            previousMessagesSize = chatMessages.size
        }
    }

    //스크롤이 최하단에 위치할 경우
    ScrollToEndCallback(listState, isReverse = true) {

        //최신 메시지 알림 영역을 제거한다
        showNewMessage = ""
        showNewMessageUserName = ""
        showNewMessageNotification = false
    }

    LazyColumn(
        modifier = modifier.pointerInput(Unit) {
            detectTapGestures(
                onTap = {
                    //키보드 숨기기
                    keyboardController?.hide()
                })
        },
        reverseLayout = true, //반대로
        state = listState,
        verticalArrangement = Arrangement.Top,
        contentPadding = PaddingValues(bottom = 8.dp)
    ) {
        //내가 전송중인 이미지 정보
        //이미지가 전송중일때만 정보를 표시
        when (uploadState) {

            is ImageUploadState.Compress -> {
                item{
                    UploadCompressImageItem(uploadState)
                }

                if (chatMessages.isNotEmpty()) scope.launch { listState.scrollToItem(0) }
            }

            is ImageUploadState.Progress -> {
                item {
                    if (uploadState.imageUploadInfoList.size == 1)
                        UploadSingleImageItem(uploadState.imageUploadInfoList.first())
                    else
                        UploadMultiImageItem(uploadState)
                }

                if (chatMessages.isNotEmpty()) scope.launch { listState.scrollToItem(0) }
            }

            else -> Unit
        }

        //메시지 이력 목록
        items(count = chatMessages.size, key = { index -> chatMessages[index].id }) { index ->
            val chatMessage = chatMessages[index]
            val messageContent = chatMessage.messageContents.first()


            // 이전 메시지 정보 (더 오래된 메시지)
            val previousMessage =
                if (index < chatMessages.size - 1) chatMessages[index + 1] else null

            // 다음 메시지 정보 (더 최근 메시지)
            val nextMessage = if (index > 0) chatMessages[index - 1] else null

            // 연속된 메시지인지 확인
            val isConsecutiveMessage = previousMessage?.let { prev ->
                prev.senderId == chatMessage.senderId &&
                        DateUtil.isSameTimeMinute(prev.timestamp, chatMessage.timestamp)
            } == true

            val showTimestamp = nextMessage?.let { next ->
                !(next.senderId == chatMessage.senderId &&
                        DateUtil.isSameTimeMinute(next.timestamp, chatMessage.timestamp))
            } != false

            when (messageContent.type) {
                //시스템 메시지는 별도 표시
                MessageType.SYSTEM -> {
                    SystemMessageItem(
                        content = messageContent.content
                    )
                }

                //채팅 메시지 표시
                else -> {
                    println()
                    // message.senderId와 일치하는 chatRoom.participants user를 찾음
                    val sender = chatRoom.participants.find {
                        it.id == chatMessage.senderId
                    } ?: User()

                    //내가 작성한 메시지
                    if (chatMessage.senderId == currentUser.id) {
                        MyMessageItem(
                            currentUser = sender,
                            participantsStatus = participantsStatus,
                            messageContents = chatMessage.messageContents,
                            timestamp = chatMessage.timestamp,
                            onNavigateToImagePager = onNavigateToImagePager,
                            showTimestamp = showTimestamp
                        )

                    } else {
                        //다른 사람이 작성한 메시지
                        OtherMessageItem(
                            sender = sender,
                            participantsStatus = participantsStatus,
                            messageContents = chatMessage.messageContents,
                            timestamp = chatMessage.timestamp,
                            onNavigateToProfile = onNavigateToProfile,
                            onNavigateToImagePager = onNavigateToImagePager,
                            showProfile = !isConsecutiveMessage,
                            showTimestamp = showTimestamp
                        )
                    }
                }
            }

            //첫번째 메시지거나 이전 메시지와 날짜가 다르다면
            // 날짜 정보 표시
            if (index == chatMessages.size - 1 || !DateUtil.isToday(
                    chatMessages[index + 1].timestamp, chatMessage.timestamp
                )
            ) {
                //중앙 날짜 정보
                FullDateItem(DateUtil.getFullDate(chatMessage.timestamp))
            }
        }
    }


    // 새 메시지 알림
    if (showNewMessageNotification) {
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 10.dp)
                .padding(5.dp),
            color = MaterialTheme.colorScheme.background,
            shape = RoundedCornerShape(10.dp),
            shadowElevation = if (isAppInDarkTheme()) 0.dp else 5.dp,
            onClick = {
                scope.launch {
                    listState.scrollToItem(0)
                    showNewMessageNotification = false
                }
            },
            border = if (isAppInDarkTheme()) BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline
            ) else null
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 10.dp, vertical = 5.dp)
                    .background(MaterialTheme.colorScheme.surface),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = showNewMessageUserName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.width(5.dp))

                Text(
                    modifier = Modifier.weight(1f, fill = false),
                    text = showNewMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                Spacer(modifier = Modifier.width(5.dp))

                Icon(
                    imageVector = Icons.Rounded.KeyboardArrowDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface
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