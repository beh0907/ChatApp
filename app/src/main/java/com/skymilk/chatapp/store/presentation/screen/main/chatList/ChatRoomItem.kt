package com.skymilk.chatapp.store.presentation.screen.main.chatList

import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.skymilk.chatapp.store.domain.model.ChatRoomWithUsers
import com.skymilk.chatapp.store.domain.model.User
import com.skymilk.chatapp.ui.theme.Black
import com.skymilk.chatapp.ui.theme.HannaPro
import com.skymilk.chatapp.ui.theme.dimens
import com.skymilk.chatapp.utils.DateUtil

@Composable
fun ChatRoomItem(
    chatRoom: ChatRoomWithUsers,
    currentUser: User,
    onChatItemClick: (ChatRoomWithUsers) -> Unit
) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .clickable {
            onChatItemClick(chatRoom)
        }
        .padding(MaterialTheme.dimens.small2)
    ) {
        //이미지 정보
        AsyncImage(
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(4.dp)),
            model = ImageRequest.Builder(
                LocalContext.current
            )
                .data(chatRoom.participants[1].profileImageUrl)
                .crossfade(true)
                .build(),
            contentDescription = null,
        )

        //채팅방 정보
        Column(
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .weight(1f)
        ) {

            Text(text = buildAnnotatedString {
                chatRoom.participants.forEach { participant ->
                    if (participant.id != currentUser.id) {
                        withStyle(
                            style = SpanStyle(
                                color = if (isSystemInDarkTheme()) Color.White else Black,
                                fontStyle = MaterialTheme.typography.bodyLarge.fontStyle,
                                fontFamily = HannaPro,

                                )
                        ) {
                            append("${participant.username} ")
                        }
                    }
                }

                //1대1이 아닌 다수의 채팅방일 경우 참여자 수 표시
                if (chatRoom.participants.size > 2) {
                    withStyle(
                        style = SpanStyle(
                            color = Color.Gray,
                            fontSize = MaterialTheme.typography.labelMedium.fontSize,
                            fontFamily = HannaPro,
                            fontWeight = FontWeight.Medium
                        )
                    ) {
                        append("${chatRoom.participants.size}")
                    }
                }
            })

            //채팅방 마지막 대화
            Text(
                text = chatRoom.lastMessage.ifBlank { "메시지가 없습니다" },
                fontFamily = HannaPro,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.DarkGray,
                maxLines = 1
            )
        }


        //시간 정보
        Text(
            text = DateUtil.getDate(chatRoom.lastMessageTimestamp),
            fontFamily = HannaPro,
            style = MaterialTheme.typography.bodyMedium,
            color = Black
        )
    }
}