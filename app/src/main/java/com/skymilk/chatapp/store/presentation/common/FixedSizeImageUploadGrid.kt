package com.skymilk.chatapp.store.presentation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.google.common.collect.Multimaps.index
import com.skymilk.chatapp.store.domain.model.MessageContent
import com.skymilk.chatapp.store.presentation.screen.main.chatRoom.state.ImageUploadState

//업로드 시 그리드 뷰
@Composable
fun FixedSizeImageUploadGrid(
    modifier: Modifier = Modifier,
    uploadState: ImageUploadState.Progress,
    maxColumnCount: Int = 3,
) {
    val images = uploadState.imageUploadInfoList
    val grid = images.redistributeLastRows(maxColumnCount)

    Box(
        modifier = modifier
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            grid.forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    row.forEach { imageUploadInfo ->
                        AsyncImage(
                            model = imageUploadInfo.imageUri,
                            contentDescription = "Upload Image",
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
        }

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
                progress = { uploadState.completedOrFailedImages.toFloat() / images.size },
                modifier = Modifier.size(40.dp),
                color = Color.White,
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = "${uploadState.completedOrFailedImages} / ${images.size}",
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

//메시지 이미지 목록 그리드
@Composable
fun FixedSizeImageMessageGrid(
    modifier: Modifier = Modifier,
    messageContents: List<MessageContent>,
    maxColumnCount: Int = 3,
    onNavigateToImageViewer: (List<String>, Int) -> Unit
) {
    val context = LocalContext.current
    val grid = messageContents.redistributeLastRows(maxColumnCount)

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        grid.map { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                row.map { image ->
                    SubcomposeAsyncImage(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .weight(1f)
                            .clip(RoundedCornerShape(4.dp))
                            .clickable {
                                onNavigateToImageViewer(messageContents.map { it.content }, messageContents.indexOf(image))
                            },
                        model = ImageRequest.Builder(context)
                            .data(image.content)
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
        }
    }
}

// 행렬 재배치 확장 함수
fun <T> List<T>.redistributeLastRows(maxColumnCount: Int): List<List<T>> {
    val grid = this.chunked(maxColumnCount).toMutableList()

    if (grid.size > 1 && grid.last().size < maxColumnCount - 1) {
        val totalItems = grid[grid.lastIndex - 1].size + grid.last().size
        val itemsPerRow = totalItems / 2

        val newPreviousRow = this.slice(
            (grid.lastIndex - 1) * maxColumnCount until
                    (grid.lastIndex - 1) * maxColumnCount + itemsPerRow
        )
        val newLastRow = this.slice(
            (grid.lastIndex - 1) * maxColumnCount + itemsPerRow until
                    this.size
        )

        grid[grid.lastIndex - 1] = newPreviousRow
        grid[grid.lastIndex] = newLastRow
    }

    return grid
}