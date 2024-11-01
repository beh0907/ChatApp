package com.skymilk.chatapp.store.presentation.screen.main.chatRoom.components

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.skymilk.chatapp.R
import com.skymilk.chatapp.store.data.dto.ParticipantStatus
import com.skymilk.chatapp.store.data.dto.MessageContent
import com.skymilk.chatapp.store.data.dto.MessageType
import com.skymilk.chatapp.store.data.dto.User
import com.skymilk.chatapp.store.presentation.common.FixedSizeImageMessageGrid
import com.skymilk.chatapp.store.presentation.common.shimmerEffect
import com.skymilk.chatapp.store.presentation.common.squircleClip
import com.skymilk.chatapp.store.presentation.utils.DateUtil
import com.skymilk.chatapp.ui.theme.Black

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun OtherMessageItem(
    sender: User,
    participantsStatus: List<ParticipantStatus>,
    messageContents: List<MessageContent>,
    timestamp: Long,
    onNavigateToProfile: (User) -> Unit,
    onNavigateToImagePager: (List<String>, Int, String, Long) -> Unit,
    showProfile: Boolean = true,  // 프로필 표시 여부
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
            verticalAlignment = Alignment.Bottom
        ) {
            if (showProfile) {
                AsyncImage(
                    model = sender.profileImageUrl.ifBlank { R.drawable.bg_default_profile },
                    contentDescription = "Profile Image",
                    modifier = Modifier
                        .size(50.dp)
                        .squircleClip()
                        .align(Alignment.Top)
                        .clickable {
                            if (sender.id.isNotBlank())
                                onNavigateToProfile(sender)
                        },
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(8.dp))
            } else {
                // 프로필 이미지 공간만큼 여백 추가
                Spacer(modifier = Modifier.width(58.dp))
            }

            Column {
                //같은 시간대가 아닐때만 유저 정보 표시
                //연속된 메시지에 대한 프로필 정보 출력 설정
                if (showProfile) {
                    Text(
                        text = sender.username.ifBlank { "퇴장한 유저입니다." },
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))
                }

                Row {
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
                                        .background(Color.White)
                                        .padding(8.dp)
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
                                        sender.username,
                                        timestamp
                                    )
                                }
                            )
                        }

                        else -> Unit
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    //시간 및 채팅 읽은 수 체크
                    Column(
                        modifier = Modifier.align(Alignment.Bottom)
                    ) {
                        val count = participantsStatus.count { timestamp > it.lastReadTimestamp }
                        if (count > 0) {
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
                }
            }
        }
    }
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun OtherMessageItemShimmer() {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp)
    ) {
        val maxWidth = maxWidth * 0.6f

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            // 프로필 이미지 자리 표시자
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .squircleClip()
                    .shimmerEffect()
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.widthIn(max = maxWidth)) {
                // 사용자 이름 자리 표시자
                Box(
                    modifier = Modifier
                        .height(20.dp)
                        .shimmerEffect()
                        .fillMaxWidth()
                )

                // 메시지 내용 자리 표시자
                Box(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .height(40.dp) // 높이는 상황에 맞게 조정
                        .shimmerEffect()
                        .fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // 시간 자리 표시자
            Box(
                modifier = Modifier
                    .height(20.dp)
                    .width(50.dp)
                    .align(Alignment.Bottom)
                    .shimmerEffect()
            )
        }
    }
}