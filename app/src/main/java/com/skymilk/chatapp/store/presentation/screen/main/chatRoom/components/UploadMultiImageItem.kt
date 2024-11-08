package com.skymilk.chatapp.store.presentation.screen.main.chatRoom.components

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
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

//이미지가 여러개일 땐 처리 완료된 수를 보여준다
@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun UploadMultiImageItem(
    uploadState: ImageUploadState.Progress
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        val maxWidth = maxWidth * 0.6f

        Box(
            modifier = Modifier
                .width(maxWidth)
                .clip(RoundedCornerShape(12.dp)),
        ) {
            // 이미지 그리드
            FixedSizeImageUploadGrid(
                imageUploadUris = uploadState.imageUploadInfoList.mapNotNull { it.imageUri },
                maxColumnCount = 3
            )

            // 오버레이 뷰
            Column(
                modifier = Modifier
                    .matchParentSize() // Box의 부모 크기에 맞춤
                    .background(Color.Black.copy(alpha = 0.5f)),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(
                    progress = { uploadState.completedOrFailedImages.toFloat() / uploadState.imageUploadInfoList.size },
                    modifier = Modifier.size(30.dp),
                    color = Color.White,
                )

                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    text = "${uploadState.completedOrFailedImages} / ${uploadState.imageUploadInfoList.size}",
                    color = Color.White,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}