package com.skymilk.chatapp.store.presentation.screen.main.chatRoom.components

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.skymilk.chatapp.store.domain.model.ChatMessage
import com.skymilk.chatapp.store.domain.model.ChatRoomWithUsers
import com.skymilk.chatapp.store.domain.model.User
import com.skymilk.chatapp.store.presentation.screen.main.chatRoom.state.ImageUploadState
import com.skymilk.chatapp.utils.DateUtil
import kotlin.random.Random

@Composable
fun ChatMessageList(
    modifier: Modifier = Modifier,
    chatRoom: ChatRoomWithUsers,
    chatChatMessages: List<ChatMessage>,
    currentUser: User,
    uploadState: ImageUploadState
) {
    val listState = rememberLazyListState()

    // 리스트 로드 후 최하단으로 스크롤
    LaunchedEffect(chatChatMessages) {
        if (chatChatMessages.isNotEmpty()) {
            listState.scrollToItem(chatChatMessages.size - 1)
        }
    }

    LazyColumn(
        modifier = modifier,
        state = listState
    ) {
        //메시지 이력 목록
        itemsIndexed(chatChatMessages, key = { _, message -> message.id }) { index, message ->

            //첫번째 메시지거나 이전 메시지와 날짜가 다르다면
            // 날짜 정보 표시
            if (index == 0 || !DateUtil.isToday(
                    chatChatMessages[index - 1].timestamp,
                    message.timestamp
                )
            ) {
                //중앙 날짜 정보
                ItemFullDate(DateUtil.getFullDate(message.timestamp))
            }

            if (message.senderId == currentUser.id) {
                //내가 작성한 메시지
                ItemMyMessage(
                    chatMessage = message
                )

            } else {
                // message.senderId와 일치하는 chatRoom.participants 내 user를 찾음
                val sender = chatRoom.participants
                    .find { it.id == message.senderId }!!

                //다른 사람이 작성한 메시지
                ItemOtherMessage(
                    chatMessage = message,
                    sender = sender
                )
            }
        }

        //내가 전송중인 이미지 정보
        //이미지가 전송중일때만 정보를 표시
        if (uploadState is ImageUploadState.Uploading) {
            item {
                ItemMyUploadImage(uploadState)
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

            if (isMyMessage) ItemMyMessageShimmer() // 사용자 메시지 로딩 아이템
            else ItemOtherMessageShimmer() // 상대방 메시지 로딩 아이템

        }
    }
}