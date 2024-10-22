package com.skymilk.chatapp.store.presentation.common

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.skymilk.chatapp.store.domain.model.MessageContent
import com.skymilk.chatapp.store.presentation.screen.main.chatRoom.state.ImageUploadState
import kotlin.math.sqrt

//LazyColumn 하위에 LazyVerticalGrid가 스크롤 중첩으로 오류가 발생할 수 있다
//그래서 modifier를 설정으로 정적 사이즈로 설정한다
@Composable
fun FixedSizeImageUploadGrid(
    modifier: Modifier = Modifier,
    maxWidth: Dp,
    uploadState: ImageUploadState.Progress,
    maxColumnCount: Int = 3,
) {
    val imageCount = uploadState.imageUploadInfoList.size
    val columnCount = calculateOptimalColumnCount(imageCount, maxColumnCount)
    val imageSize = maxWidth / columnCount
    val rows = (imageCount + columnCount - 1) / columnCount
    val gridHeight = imageSize * rows

    Box(
        modifier = modifier
            .width(maxWidth)
            .height(gridHeight)
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(columnCount),
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            userScrollEnabled = false
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
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator(
                progress = { uploadState.completedOrFailedImages.toFloat() / imageCount },
                modifier = Modifier.size(50.dp),
                color = Color.White,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${uploadState.completedOrFailedImages} / $imageCount",
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun FixedSizeImageMessageGrid(
    modifier: Modifier = Modifier,
    maxWidth: Dp,
    messageContents: List<MessageContent>,
    maxColumnCount: Int = 3,
    imageContent: @Composable (String, Int) -> Unit
) {
    val imageCount = messageContents.size
    val columnCount = calculateOptimalColumnCount(imageCount, maxColumnCount)
    val imageSize = maxWidth / columnCount
    val rows = (imageCount + columnCount - 1) / columnCount
    val gridHeight = imageSize * rows

    Box(
        modifier = modifier
            .width(maxWidth)
            .height(gridHeight)
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(columnCount),
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            userScrollEnabled = false
        ) {
            itemsIndexed(messageContents) { index, messageContent ->
                imageContent(messageContent.content, index)
            }
        }
    }
}


fun calculateOptimalColumnCount(imageCount: Int, maxColumnCount: Int): Int {
    return when {
        imageCount <= 1 -> 1
        imageCount <= maxColumnCount -> imageCount
        else -> {
            val sqrt = sqrt(imageCount.toFloat()).toInt()
            sqrt.coerceAtMost(maxColumnCount)
        }
    }
}