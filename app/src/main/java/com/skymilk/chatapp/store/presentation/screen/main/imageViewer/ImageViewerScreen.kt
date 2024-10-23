package com.skymilk.chatapp.store.presentation.screen.main.imageViewer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Filter
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material.icons.rounded.Wallpaper
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable

@Composable
fun ImageViewerScreen(
    modifier: Modifier = Modifier,
    imageUrls: List<String>, // 이미지 URL 리스트
    initialPage: Int, // 페이저 시작 위치
    onNavigateToBack: () -> Unit,
) {
    //시작 위치 설정
    val pagerState = rememberPagerState(initialPage = initialPage) { imageUrls.size }

    //썸네일 표시 여부
    var visibleThumbnails by remember { mutableStateOf(true) }

    //페이지마다 줌 상태를 저장하고 싶다면 사용
//    val zoomableStates = imageUrls.map { rememberZoomState() }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            userScrollEnabled = imageUrls.size > 1, // 이미지가 한장일땐 스크롤X
        ) { page ->
            AsyncImage(
                modifier = Modifier
                    .fillMaxSize()
                    .zoomable(zoomState = rememberZoomState(),
                        onTap = { visibleThumbnails = !visibleThumbnails }),
                model = imageUrls[page],
                contentDescription = null,
            )
        }

        //상단 메뉴
        TopSection(
            modifier = Modifier, onNavigateToBack = onNavigateToBack
        )

        //하단 썸네일 목록
        if (imageUrls.size > 1) {
            AnimatedVisibility(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter),
                visible = visibleThumbnails
            ) {

                ThumbnailSection(
                    pagerState = pagerState, imageUrls = imageUrls
                )
            }
        }
    }
}

@Composable
fun TopSection(
    modifier: Modifier = Modifier, onNavigateToBack: () -> Unit
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
    modifier: Modifier = Modifier, pagerState: PagerState, imageUrls: List<String>
) {
    // 코루틴 스코프 생성
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    //페이지가 이동할 때마다 썸네일 목록도 스크롤
    //선택한 아이템이 중앙으로 올 수 있도록 설정
    LaunchedEffect(pagerState.currentPage) {

        val itemInfo =
            listState.layoutInfo.visibleItemsInfo.firstOrNull { it.index == pagerState.currentPage }
        if (itemInfo != null) {
            val center = listState.layoutInfo.viewportEndOffset / 2
            val childCenter = itemInfo.offset + itemInfo.size / 2
            listState.animateScrollBy((childCenter - center).toFloat())
        } else {
            listState.animateScrollToItem(pagerState.currentPage)
        }
    }

    Column(
        modifier = modifier.background(Color.Black.copy(alpha = 0.6f))
    ) {
        //썸네일 목록
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(8.dp),
            state = listState
        ) {
            items(imageUrls.size) { index ->
                ThumbnailItem(imageUrl = imageUrls[index],
                    isSelected = pagerState.currentPage == index,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.scrollToPage(index)
                        }
                    })
            }
        }


        Row(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = Icons.Rounded.Filter, contentDescription = null, tint = Color.White)

            Spacer(modifier = Modifier.width(8.dp))

            //이미지 목록 인덱스
            Text(
                text = "${pagerState.currentPage + 1} / ${imageUrls.size}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

