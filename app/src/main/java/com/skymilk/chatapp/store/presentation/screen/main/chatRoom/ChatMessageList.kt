package com.skymilk.chatapp.store.presentation.screen.main.chatRoom

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.util.fastFilter
import com.skymilk.chatapp.store.domain.model.ChatRoomWithUsers
import com.skymilk.chatapp.store.domain.model.Message
import com.skymilk.chatapp.store.domain.model.User
import com.skymilk.chatapp.utils.DateUtil

@Composable
fun ChatMessageList(
    modifier: Modifier = Modifier,
    chatRoom: ChatRoomWithUsers,
    chatMessages: List<Message>,
    currentUser: User
) {
    val listState = rememberLazyListState()

    // 리스트 로드 후 최하단으로 스크롤
    LaunchedEffect(chatMessages) {
        if (chatMessages.isNotEmpty()) {
            listState.scrollToItem(chatMessages.size - 1)
        }
    }

    LazyColumn(
        modifier = modifier,
        state = listState
    ) {
        itemsIndexed(chatMessages, key = { _, message -> message.id }) { index, message ->

            //첫번째 메시지거나 이전 메시지와 날짜가 다르다면
            // 날짜 정보 표시
            if (index == 0 || !DateUtil.isToday(chatMessages[index - 1].timestamp, message.timestamp)) {
                ItemFullDate(DateUtil.getFullDate(message.timestamp))
            }

            if (message.senderId == currentUser.id) {
                //내가 작성한 메시지라면
                ItemMyMessage(
                    message = message
                )

            } else {
                // message.senderId와 일치하는 chatRoom.participants 내 user를 찾음
                val sender = chatRoom.participants
                    .find { it.id == message.senderId }!!

                //다른 사람이 작성한 메시지라면
                ItemOtherMessage(
                    message = message,
                    sender = sender
                )
            }
        }
    }
}