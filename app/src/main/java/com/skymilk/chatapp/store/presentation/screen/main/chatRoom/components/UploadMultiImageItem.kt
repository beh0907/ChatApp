package com.skymilk.chatapp.store.presentation.screen.main.chatRoom.components

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.skymilk.chatapp.store.presentation.common.FixedSizeImageUploadGrid
import com.skymilk.chatapp.store.presentation.screen.main.chatRoom.state.ImageUploadState
import com.skymilk.chatapp.store.presentation.utils.DateUtil

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun UploadMultiImageItem(
    uploadState: ImageUploadState.Progress
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
            Text(
                text = DateUtil.getTime(),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(end = 8.dp)
            )

            Box(
                modifier = Modifier
                    .width(maxWidth)
                    .clip(RoundedCornerShape(12.dp))
            ) {
                // 이미지 그리드
                FixedSizeImageUploadGrid(
                    imageUploadInfoList = uploadState.imageUploadInfoList,
                    maxColumnCount = 3
                )

                // 오버레이 뷰
                Column(
                    modifier = Modifier
                        .matchParentSize() // Box의 부모 크기에 맞춤
                        .background(Color.Black.copy(alpha = 0.5f))
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(
                        progress = { uploadState.completedOrFailedImages.toFloat() / uploadState.imageUploadInfoList.size },
                        modifier = Modifier.size(40.dp),
                        color = Color.White,
                    )

                    Spacer(modifier = Modifier.height(5.dp))

                    Text(
                        text = "${uploadState.completedOrFailedImages} / ${uploadState.imageUploadInfoList.size}",
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}