package com.skymilk.chatapp.store.presentation.screen.main.chatRoom.components

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.skymilk.chatapp.store.presentation.screen.main.chatRoom.state.ImageUploadState
import com.skymilk.chatapp.store.presentation.utils.DateUtil
import kotlin.math.sqrt

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

            Surface(
                shape = RoundedCornerShape(12.dp),
                shadowElevation = 4.dp,
                color = Color(0xFF90CAF9),
                modifier = Modifier.widthIn(max = maxWidth)
            ) {
                // 이미지 그리드
                FixedSizeImageGrid(
                    maxWidth = maxWidth,
                    uploadState = uploadState,
                    maxColumnCount = 3
                )
            }
        }
    }
}

//LazyColumn 하위에 LazyVerticalGrid가 스크롤 중첩으로 오류가 발생할 수 있다
//그래서 modifier를 설정으로 정적 사이즈로 설정한다
@Composable
fun FixedSizeImageGrid(
    maxWidth: Dp,
    uploadState: ImageUploadState.Progress,
    maxColumnCount: Int = 3
) {
    val imageCount = uploadState.imageUploadInfoList.size
    val columnCount = calculateOptimalColumnCount(imageCount, maxColumnCount)
    val imageSize = maxWidth / columnCount
    val rows = (imageCount + columnCount - 1) / columnCount
    val gridHeight = imageSize * rows

    Box(
        modifier = Modifier
            .width(maxWidth)
            .height(gridHeight)
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(columnCount),
            modifier = Modifier.fillMaxSize()
        ) {
            items(uploadState.imageUploadInfoList) { imageUploadInfo ->
                AsyncImage(
                    model = imageUploadInfo.imageUri,
                    contentDescription = "Chat Image",
                    modifier = Modifier
                        .aspectRatio(1f)
                        .fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }

        // 프로그레스 및 텍스트
        Column(
            modifier = Modifier
                .width(maxWidth)
                .height(gridHeight)
                .background(Color.Black.copy(alpha = 0.5f))
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator(
                progress = { (uploadState.completedOrFailedImages / uploadState.imageUploadInfoList.size).toFloat() },
                modifier = Modifier.size(50.dp),
                color = Color.White,
                trackColor = ProgressIndicatorDefaults.circularIndeterminateTrackColor,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${uploadState.completedOrFailedImages} / ${uploadState.imageUploadInfoList.size}",
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}


fun calculateOptimalColumnCount(imageCount: Int, maxColumnCount: Int): Int {
    return when {
        imageCount <= maxColumnCount -> imageCount
        else -> {
            val sqrt = sqrt(imageCount.toFloat()).toInt()
            sqrt.coerceAtMost(maxColumnCount)
        }
    }
}