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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.NotificationsOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.skymilk.chatapp.store.domain.model.ChatRoomWithUsers
import com.skymilk.chatapp.store.domain.model.User
import com.skymilk.chatapp.store.presentation.common.ChatProfileGrid
import com.skymilk.chatapp.store.presentation.common.shimmerEffect
import com.skymilk.chatapp.store.presentation.common.squircleClip
import com.skymilk.chatapp.store.presentation.utils.DateUtil
import com.skymilk.chatapp.ui.theme.dimens

@Composable
fun ChatRoomItem(
    chatRoom: ChatRoomWithUsers,
    currentUser: User,
    isAlarmsDisabled: Boolean,
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

                Row(
                    modifier = Modifier.weight(weight = 1f),
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

                    Spacer(Modifier.width(5.dp))

                    //다수의 채팅방은 경우 참여자 수 표시
                    Text(
                        text = "${chatRoom.participants.size}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Gray,
                    )
                }

                Spacer(Modifier.width(5.dp))


                //시간 정보
                Text(
                    text = DateUtil.getDate(chatRoom.lastMessageTimestamp),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            Spacer(Modifier.height(5.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                //채팅방 마지막 대화
                Text(
                    modifier = Modifier.weight(weight = 1f),
                    text = chatRoom.lastMessage.ifBlank { "메시지가 없습니다" },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                Spacer(Modifier.width(5.dp))

                //알람 비활성화 여부 표시
                if (isAlarmsDisabled) {
                    Icon(imageVector = Icons.Outlined.NotificationsOff, contentDescription = null)
                }

            }
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