package com.skymilk.chatapp.store.presentation.screen.main.chatRoom.components

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.skymilk.chatapp.store.data.dto.ParticipantStatus
import com.skymilk.chatapp.store.data.dto.MessageContent
import com.skymilk.chatapp.store.data.dto.MessageType
import com.skymilk.chatapp.store.data.dto.User
import com.skymilk.chatapp.store.presentation.common.FixedSizeImageMessageGrid
import com.skymilk.chatapp.store.presentation.common.shimmerEffect
import com.skymilk.chatapp.store.presentation.utils.DateUtil
import com.skymilk.chatapp.ui.theme.Black

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun MyMessageItem(
    currentUser: User,
    participantsStatus: List<ParticipantStatus>,
    messageContents: List<MessageContent>,
    timestamp: Long,
    onNavigateToImagePager: (List<String>, Int, String, Long) -> Unit,
    showTimestamp: Boolean = true // 시간 표시 여부
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp)
    ) {
        val maxWidth = maxWidth * 0.6f

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.Bottom
        ) {
            //시간 및 채팅 읽은 수 체크
            Column(
                modifier = Modifier.padding(end = 8.dp),
                horizontalAlignment = Alignment.End
            ) {
                val count = participantsStatus.count { timestamp > it.lastReadTimestamp }
                if (count > 0 && participantsStatus.size > 1) {
                    //읽지 않은 유저 수 정보
                    Text(
                        text = count.toString(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                //시간 정보
                //연속된 메시지에 대한 시간 정보 출력 설정
                if (showTimestamp) {
                    Text(
                        text = DateUtil.getTime(timestamp),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            when (messageContents.first().type) {
                //텍스트 타입
                MessageType.TEXT -> {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        shadowElevation = 4.dp,
                        modifier = Modifier.widthIn(max = maxWidth)
                    ) {
                        Text(
                            color = Black,
                            text = messageContents.first().content,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier
                                .background(Color(0xFF90CAF9))
                                .padding(8.dp),
                        )
                    }
                }

                //이미지 타입
                MessageType.IMAGE -> {
                    // 이미지 그리드
                    FixedSizeImageMessageGrid(
                        modifier = Modifier
                            .width(maxWidth)
                            .clip(RoundedCornerShape(12.dp)),
                        messageContents = messageContents,
                        maxColumnCount = 3,
                        onNavigateToImagePager = { imageUrls, initialPage ->
                            onNavigateToImagePager(
                                imageUrls,
                                initialPage,
                                currentUser.username,
                                timestamp
                            )
                        }
                    )
                }

                else -> Unit
            }
        }
    }
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun MyMessageItemShimmer() {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.Bottom
        ) {
            // 시간 자리 표시자
            Box(
                modifier = Modifier
                    .height(20.dp)
                    .width(50.dp)
                    .padding(end = 8.dp)
                    .shimmerEffect()
            )

            // 메시지 내용 자리 표시자
            Box(
                modifier = Modifier
                    .height(40.dp) // 높이는 상황에 맞게 조정
                    .shimmerEffect()
                    .fillMaxWidth(0.6f)
            )
        }
    }
}