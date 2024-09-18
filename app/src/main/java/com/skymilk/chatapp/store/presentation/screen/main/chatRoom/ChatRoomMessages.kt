package com.skymilk.chatapp.store.presentation.screen.main.chatRoom

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.util.fastFilter
import com.skymilk.chatapp.store.domain.model.ChatRoomWithUsers
import com.skymilk.chatapp.store.domain.model.Message
import com.skymilk.chatapp.store.domain.model.User

@Composable
fun ChatRoomMessages(
    modifier: Modifier = Modifier,
    chatRoom: ChatRoomWithUsers,
    chatMessages: List<Message>,
    currentUser: User
) {
    LazyColumn(
        modifier = modifier
    ) {
        items(chatMessages, key = { message -> message.id }) { message ->
            if (message.senderId == currentUser.id) {
                //내가 작성한 메시지라면
                MyMessageItem(
                    message = message
                )

            } else {
                // message.senderId와 일치하는 chatRoom.participants 내 user를 찾음
                val sender = chatRoom.participants
                    .fastFilter { it.id == message.senderId }
                    .firstOrNull()

                //다른 사람이 작성한 메시지라면
                OtherMessageItem(
                    message = message,
                    sender = sender
                )
            }
        }
    }
}