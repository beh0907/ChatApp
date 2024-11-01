package com.skymilk.chatapp.store.presentation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.skymilk.chatapp.R
import com.skymilk.chatapp.store.domain.model.ImageUploadInfo
import com.skymilk.chatapp.store.data.dto.MessageContent
import com.skymilk.chatapp.store.data.dto.User

//채팅방 목록 프로필 이미지 그리드
@Composable
fun ChatProfileGrid(
    otherParticipants: List<User>,
) {
    val boxSize = 50.dp

    Box(modifier = Modifier.size(boxSize)) {
        otherParticipants.forEachIndexed { index, participant ->
            val (widthFraction, heightFraction, xOffset, yOffset) = when (otherParticipants.size) {
                1 -> listOf(1f, 1f, 0f, 0f)
                2 -> when (index) {
                    0 -> listOf(0.6f, 0.6f, 0f, 0f)
                    else -> listOf(0.6f, 0.6f, 0.4f, 0.4f)
                }

                3 -> when (index) {
                    0 -> listOf(0.5f, 0.5f, 0.25f, 0f)
                    1 -> listOf(0.5f, 0.5f, 0f, 0.5f)
                    else -> listOf(0.5f, 0.5f, 0.5f, 0.5f)
                }

                else -> when (index) {
                    0 -> listOf(0.5f, 0.5f, 0f, 0f)
                    1 -> listOf(0.5f, 0.5f, 0.5f, 0f)
                    2 -> listOf(0.5f, 0.5f, 0f, 0.5f)
                    else -> listOf(0.5f, 0.5f, 0.5f, 0.5f)
                }
            }

            AsyncImage(
                model = participant.profileImageUrl.ifBlank { R.drawable.bg_default_profile },
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(boxSize * widthFraction, boxSize * heightFraction)
                    .offset(x = boxSize * xOffset, y = boxSize * yOffset)
                    .squircleClip()
            )
        }
    }
}

//이미지 업로드 메시지 그리드 뷰
@Composable
fun FixedSizeImageUploadGrid(
    imageUploadInfoList: List<ImageUploadInfo>,
    maxColumnCount: Int = 3,
) {
    val grid = imageUploadInfoList.redistributeLastRows(maxColumnCount)

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
}

//메시지 이미지 목록 그리드
@Composable
fun FixedSizeImageMessageGrid(
    modifier: Modifier = Modifier,
    messageContents: List<MessageContent>,
    maxColumnCount: Int = 3,
    onNavigateToImagePager: (List<String>, Int) -> Unit
) {
    LocalContext.current
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
                                onNavigateToImagePager(
                                    messageContents.map { it.content },
                                    messageContents.indexOf(image)
                                )
                            },
                        model = image.content,
                        contentDescription = null,
                        loading = {
                            Box(
                                modifier = Modifier
                                    .aspectRatio(1f)
                                    .fillMaxSize()
                                    .shimmerEffect()
                            )
                        },
                        error = {
                            Icon(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(MaterialTheme.colorScheme.onSurface),
                                painter = painterResource(R.drawable.ic_warning),
                                tint = MaterialTheme.colorScheme.surface,
                                contentDescription = null
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