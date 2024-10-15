package com.skymilk.chatapp.store.presentation.screen.main.chatRoomList.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastFirst
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.skymilk.chatapp.store.domain.model.ChatRoomWithUsers
import com.skymilk.chatapp.store.domain.model.User
import com.skymilk.chatapp.store.presentation.common.shimmerEffect
import com.skymilk.chatapp.ui.theme.dimens
import com.skymilk.chatapp.utils.DateUtil

@Composable
fun ChatRoomItem(
    chatRoom: ChatRoomWithUsers,
    currentUser: User,
    onChatItemClick: (String) -> Unit
) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .clickable {
            onChatItemClick(chatRoom.id)
        }
        .padding(MaterialTheme.dimens.small2)
    ) {

        //타인의 이미지를 찾아 적용
        val image = chatRoom.participants.fastFirst { it.id != currentUser.id }.profileImageUrl

        //이미지 정보
        AsyncImage(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape),
            model = ImageRequest.Builder(
                LocalContext.current
            )
                .data(image)
                .crossfade(true)
                .build(),
            contentScale = ContentScale.Crop,
            contentDescription = null,
        )

        //채팅방 정보
        Column(
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = buildAnnotatedString {

                //참여자 이름 정보 표시
                chatRoom.participants.forEach { participant ->
                    if (participant.id != currentUser.id) { // 내 이름은 표시 X
                        withStyle(
                            style = SpanStyle(
                                color = MaterialTheme.colorScheme.onSurface,
                                fontStyle = MaterialTheme.typography.bodyLarge.fontStyle,
                                fontWeight = FontWeight.Bold
                            )
                        ) {
                            append("${participant.username} ")
                        }
                    }
                }


                //다수의 채팅방은 경우 참여자 수 표시
                withStyle(
                    style = SpanStyle(
                        color = Color.Gray,
                        fontSize = MaterialTheme.typography.labelMedium.fontSize,
                        fontWeight = FontWeight.Medium
                    )
                ) {
                    append("${chatRoom.participants.size}")
                }
            })

            Spacer(Modifier.height(5.dp))

            //채팅방 마지막 대화
            Text(
                text = chatRoom.lastMessage.ifBlank { "메시지가 없습니다" },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 1
            )
        }


        Column(
            horizontalAlignment = Alignment.End
        ) {
            //시간 정보
            Text(
                text = DateUtil.getDate(chatRoom.lastMessageTimestamp),
                style = MaterialTheme.typography.bodyMedium,
            )
        }

    }
}

@Composable
fun ChatRoomItemShimmer() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(MaterialTheme.dimens.small2)
    ) {
        // 이미지 정보
        Box(
            modifier = Modifier
                .size(50.dp)
                .shimmerEffect()
                .clip(CircleShape)
        )

        // 채팅방 정보
        Column(
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .weight(1f)
        ) {
            // 사용자 이름 자리 표시자
            Box(
                modifier = Modifier
                    .height(20.dp)
                    .shimmerEffect()
                    .fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 마지막 대화 자리 표시자
            Box(
                modifier = Modifier
                    .height(20.dp)
                    .shimmerEffect()
                    .fillMaxWidth()
            )
        }

        // 시간 정보 자리 표시자
        Box(
            modifier = Modifier
                .height(20.dp)
                .width(50.dp)
                .shimmerEffect()
        )
    }
}