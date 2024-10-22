package com.skymilk.chatapp.store.presentation.screen.main.chatRoom.components

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.google.common.collect.Multimaps.index
import com.skymilk.chatapp.store.domain.model.MessageContent
import com.skymilk.chatapp.store.domain.model.MessageType
import com.skymilk.chatapp.store.presentation.common.FixedSizeImageMessageGrid
import com.skymilk.chatapp.store.presentation.common.shimmerEffect
import com.skymilk.chatapp.store.presentation.utils.DateUtil
import com.skymilk.chatapp.ui.theme.Black

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun MyMessageItem(
    messageContents: List<MessageContent>,
    timestamp: Long,
    onNavigateToImageViewer: (List<String>, Int) -> Unit
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
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = DateUtil.getTime(timestamp),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(end = 8.dp),
            )


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
                        modifier = Modifier.clip(RoundedCornerShape(12.dp)),
                        maxWidth = maxWidth,
                        messageContents = messageContents,
                        maxColumnCount = 3
                    ) { imageUrl, index ->
                        SubcomposeAsyncImage(
                            modifier = Modifier
                                .aspectRatio(1f)
                                .fillMaxSize()
                                .clip(RoundedCornerShape(4.dp))
                                .clickable {
                                    onNavigateToImageViewer(messageContents.map { it.content }, index)
                                },
                            model = ImageRequest.Builder(context)
                                .data(imageUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = null,
                            loading = {
                                Box(
                                    modifier = Modifier
                                        .aspectRatio(1f)
                                        .fillMaxSize()
                                        .shimmerEffect()
                                )
                            },
                            contentScale = ContentScale.Crop // 이미지의 크기 조정
                        )
                    }
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
        maxWidth * 0.6f

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