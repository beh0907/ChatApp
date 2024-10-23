package com.skymilk.chatapp.store.presentation.screen.main.imageViewer

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.google.common.collect.Multimaps.index
import com.jvziyaoyao.scale.zoomable.pager.ZoomablePagerState
import com.jvziyaoyao.scale.zoomable.pager.rememberZoomablePagerState
import kotlinx.coroutines.launch

@Composable
fun ImageViewerScreen(
    modifier: Modifier = Modifier,
    imageUrls: List<String>, // 이미지 URL 리스트
    initialPage: Int = 0, // 페이저 시작 위치
    onNavigateToBack: () -> Unit,
) {
    //시작 위치 설정
    val pagerState = rememberZoomablePagerState(initialPage = initialPage) { imageUrls.size }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        CustomZoomablePager(
            state = pagerState,
            userScrollEnabled = imageUrls.size > 1, // 이미지가 한장일땐 스크롤X
        ) { page ->
            val imageRequest = ImageRequest.Builder(LocalContext.current)
                .data(imageUrls[page])
                .size(Size.ORIGINAL)
                .build()
            val painter = rememberAsyncImagePainter(imageRequest)

            if (painter.intrinsicSize.isSpecified) {
                ZoomablePolicy(intrinsicSize = painter.intrinsicSize) { _ ->
                    Image(
                        modifier = Modifier.fillMaxSize(),
                        painter = painter,
                        contentDescription = null
                    )
                }
            } else {
                Box(modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }

        //상단 메뉴
        TopSection(
            modifier = Modifier, onNavigateToBack = onNavigateToBack
        )

        //하단 썸네일 목록
        if (imageUrls.size > 1) {
            ThumbnailSection(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter),
                pagerState = pagerState,
                imageUrls = imageUrls
            )
        }
    }
}

@Composable
fun TopSection(
    modifier: Modifier = Modifier,
    onNavigateToBack: () -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth()
    ) {
        IconButton(onClick = { onNavigateToBack() }) {
            Icon(
                imageVector = Icons.Rounded.Close,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun ThumbnailSection(
    modifier: Modifier = Modifier,
    pagerState: ZoomablePagerState,
    imageUrls: List<String>
) {
    // 코루틴 스코프 생성
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    //페이지가 이동할 때마다 썸네일 목록도 스크롤
    LaunchedEffect(pagerState.currentPage) {

        val itemInfo = listState.layoutInfo.visibleItemsInfo.firstOrNull { it.index == pagerState.currentPage }
        if (itemInfo != null) {
            val center = listState.layoutInfo.viewportEndOffset / 2
            val childCenter = itemInfo.offset + itemInfo.size / 2
            listState.animateScrollBy((childCenter - center).toFloat())
        } else {
            listState.animateScrollToItem(pagerState.currentPage)
        }
    }

    Column(
        modifier = modifier
    ) {
        //썸네일 목록
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Black.copy(alpha = 0.5f)),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(8.dp),
            state = listState
        ) {
            items(imageUrls.size) { index ->
                ThumbnailItem(
                    imageUrl = imageUrls[index],
                    isSelected = pagerState.currentPage == index,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.scrollToPage(index)
                        }
                    }
                )
            }
        }

        //이미지 목록 인덱스
        Text(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(16.dp),
            text = "${pagerState.currentPage + 1} / ${imageUrls.size}",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

