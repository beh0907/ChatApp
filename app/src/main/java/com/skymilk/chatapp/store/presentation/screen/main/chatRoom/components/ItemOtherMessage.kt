package com.skymilk.chatapp.store.presentation.screen.main.chatRoom.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
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
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.skymilk.chatapp.store.domain.model.ChatMessage
import com.skymilk.chatapp.store.domain.model.MessageType
import com.skymilk.chatapp.store.domain.model.User
import com.skymilk.chatapp.store.presentation.common.shimmerEffect
import com.skymilk.chatapp.ui.theme.Black
import com.skymilk.chatapp.ui.theme.HannaPro
import com.skymilk.chatapp.utils.DateUtil

@Composable
fun ItemOtherMessage(chatMessage: ChatMessage, sender: User) {
    val context = LocalContext.current

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
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(sender.profileImageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "Profile Image",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .align(Alignment.Top),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.widthIn(max = maxWidth)) {
                Text(
                    text = sender.username,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    fontFamily = HannaPro
                )

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    shadowElevation = 4.dp,
                    color = Color.White,
                    modifier = Modifier.widthIn(max = maxWidth)
                ) {
                    when (chatMessage.type) {
                        //텍스트 타입
                        MessageType.TEXT -> {
                            Text(
                                color = Black,
                                text = chatMessage.content,
                                modifier = Modifier.padding(8.dp),
                                fontFamily = HannaPro
                            )
                        }

                        //이미지 타입
                        MessageType.IMAGE -> {
                            SubcomposeAsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(chatMessage.content)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = null,
                                modifier = Modifier.size(maxWidth),
                                loading = {
                                    Box(modifier = Modifier
                                        .fillMaxSize()
                                        .shimmerEffect())
                                },
                                contentScale = ContentScale.Crop // 이미지의 크기 조정
                            )
                        }

                        MessageType.VIDEO -> {
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = DateUtil.getTime(chatMessage.timestamp),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.align(Alignment.Bottom),
                fontFamily = HannaPro
            )
        }
    }
}

@Composable
fun ItemOtherMessageShimmer() {
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
                    .clip(CircleShape)
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