package com.skymilk.chatapp.store.presentation.screen.main.chatRoom

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.skymilk.chatapp.store.domain.model.Message

@Composable
fun MyMessageItem(
    modifier: Modifier = Modifier,
    message: Message
) {
    // 내가 보낸 메시지의 UI 스타일
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.End
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(0.6f),
            contentAlignment = Alignment.CenterEnd
        ) {
            Text(
                text = message.content,
                modifier = Modifier
                    .background(
                        color = Color(0xFFDCF8C6), // 내 메시지에 대한 배경 색상
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(8.dp),
                color = Color.Black
            )
        }
    }
}