package com.skymilk.chatapp.store.presentation.screen.main.chatRoom.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.skymilk.chatapp.store.domain.model.ChatMessage
import com.skymilk.chatapp.store.domain.model.MessageType
import com.skymilk.chatapp.ui.theme.Black
import com.skymilk.chatapp.ui.theme.HannaPro
import com.skymilk.chatapp.utils.DateUtil

@Composable
fun ItemMyMessage(chatMessage: ChatMessage) {
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
                text = DateUtil.getTime(chatMessage.timestamp),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(end = 8.dp),
                fontFamily = HannaPro
            )

            Surface(
                shape = RoundedCornerShape(12.dp),
                shadowElevation = 4.dp,
                color = Color(0xFF90CAF9),
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
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(chatMessage.content)
                                .crossfade(true)
                                .build(),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit // 이미지의 크기 조정
                        )
                    }

                    MessageType.VIDEO -> {
                    }
                }
            }
        }
    }
}