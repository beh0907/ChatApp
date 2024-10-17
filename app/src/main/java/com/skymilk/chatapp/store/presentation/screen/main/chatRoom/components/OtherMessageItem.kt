package com.skymilk.chatapp.store.presentation.screen.main.chatRoom.components

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.skymilk.chatapp.R
import com.skymilk.chatapp.store.domain.model.ChatMessage
import com.skymilk.chatapp.store.domain.model.MessageType
import com.skymilk.chatapp.store.domain.model.User
import com.skymilk.chatapp.store.presentation.common.shimmerEffect
import com.skymilk.chatapp.store.presentation.common.squircleClip
import com.skymilk.chatapp.store.presentation.utils.DateUtil
import com.skymilk.chatapp.ui.theme.Black

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun OtherMessageItem(
    chatMessage: ChatMessage,
    sender: User,
    onNavigateToProfile: (User) -> Unit,
    onNavigateToImageViewer: (String) -> Unit
) {
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
                    .data(
                        if (sender.profileImageUrl.isNullOrBlank()) R.drawable.bg_default_profile
                        else sender.profileImageUrl
                    )
                    .decoderFactory(SvgDecoder.Factory())
                    .build(),
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

            Column {
                Text(
                    text = sender.username.ifBlank { "퇴장한 유저입니다." },
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row {
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
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }

                            //이미지 타입
                            MessageType.IMAGE -> {
                                SubcomposeAsyncImage(
                                    modifier = Modifier
                                        .widthIn(max = maxWidth)
                                        .heightIn(max = maxWidth)
                                        .clickable {
                                            onNavigateToImageViewer(chatMessage.content)
                                        },
                                    model = ImageRequest.Builder(context)
                                        .data(chatMessage.content)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = null,
                                    loading = {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .shimmerEffect()
                                        )
                                    },
                                    contentScale = ContentScale.FillWidth // 이미지의 크기 조정
                                )
                            }

                            else -> Unit
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = DateUtil.getTime(chatMessage.timestamp),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.align(Alignment.Bottom)
                    )
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