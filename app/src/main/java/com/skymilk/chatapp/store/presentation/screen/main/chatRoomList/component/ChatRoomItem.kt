package com.skymilk.chatapp.store.presentation.screen.main.chatRoomList.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.skymilk.chatapp.store.domain.model.ChatRoomWithUsers
import com.skymilk.chatapp.store.domain.model.User
import com.skymilk.chatapp.store.presentation.common.shimmerEffect
import com.skymilk.chatapp.store.presentation.common.squircleClip
import com.skymilk.chatapp.store.presentation.utils.DateUtil
import com.skymilk.chatapp.ui.theme.dimens

@Composable
fun ChatRoomItem(
    chatRoom: ChatRoomWithUsers,
    currentUser: User,
    onChatItemClick: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onChatItemClick(chatRoom.id)
            }
            .padding(MaterialTheme.dimens.small1),
    ) {

        //나를 제외한 나머지 참여자
        val otherParticipants = chatRoom.participants.filter { it.id != currentUser.id }


        //채팅방 참여 유저 프로필 그리드
        ChatProfileGrid(otherParticipants = otherParticipants.take(4))


        //채팅방 정보
        Column(
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .weight(1f),
            verticalArrangement = Arrangement.Center
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                //참여자 이름 정보 표시
                Text(
                    modifier = Modifier.weight(weight = 1f, fill = false),
                    text = otherParticipants
                        .filter { it.id != currentUser.id }
                        .joinToString(", ") { it.username },
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                //다수의 채팅방은 경우 참여자 수 표시
                Text(
                    text = "${chatRoom.participants.size}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

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
fun ChatProfileGrid(
    otherParticipants: List<User>,
) {
    val boxSize = 50.dp

    Box(modifier = Modifier.size(boxSize)) {
        otherParticipants.forEachIndexed { index, user ->
            val (widthFraction, heightFraction, xOffset, yOffset) = when (otherParticipants.size) {
                1 -> listOf(1f, 1f, 0f, 0f)
                2 -> when (index) {
                    0 -> listOf(0.6f, 0.6f, 0f, 0f)
                    else -> listOf(0.6f, 0.6f, 0.4f, 0.4f)
                }

                3 -> when (index) {
                    0 -> listOf(0.5f, 0.5f, 0.25f, 0f)
                    1 -> listOf(0.5f, 0.5f, 0f, 0.5f)
                    else -> listOf(0.5f, 0.5f, 0.5f, 0.5f)
                }

                else -> when (index) {
                    0 -> listOf(0.5f, 0.5f, 0f, 0f)
                    1 -> listOf(0.5f, 0.5f, 0.5f, 0f)
                    2 -> listOf(0.5f, 0.5f, 0f, 0.5f)
                    else -> listOf(0.5f, 0.5f, 0.5f, 0.5f)
                }
            }

            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(user.profileImageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(boxSize * widthFraction, boxSize * heightFraction)
                    .offset(x = boxSize * xOffset, y = boxSize * yOffset)
                    .squircleClip()
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
                .squircleClip()
                .shimmerEffect()
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