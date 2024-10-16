package com.skymilk.chatapp.store.presentation.screen.main.chatRoomList.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.skymilk.chatapp.store.domain.model.ChatRoomWithUsers
import com.skymilk.chatapp.store.domain.model.User
import com.skymilk.chatapp.store.presentation.common.squircleClip
import com.skymilk.chatapp.ui.theme.dimens
import com.skymilk.chatapp.store.presentation.utils.DateUtil

@Composable
fun ChatSoloRoomItem(
    chatRoom: ChatRoomWithUsers,
    currentUser: User,
    onChatItemClick: (String) -> Unit
) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .clickable {
            onChatItemClick(chatRoom.id)
        }
        .padding(MaterialTheme.dimens.small1),
    ) {
        //나의 이미지 적용
        val image = chatRoom.participants.first().profileImageUrl

        //이미지 정보
        AsyncImage(
            modifier = Modifier
                .size(50.dp)
                .squircleClip(),
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


            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(
                            MaterialTheme.colorScheme.primary
                        ),
                    text = " 나 ",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.surface
                )

                Spacer(Modifier.width(5.dp))

                Text(
                    text = currentUser.username,
                    fontStyle = MaterialTheme.typography.bodyLarge.fontStyle,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    maxLines = 1
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