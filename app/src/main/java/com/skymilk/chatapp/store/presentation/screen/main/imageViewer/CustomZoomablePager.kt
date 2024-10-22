package com.skymilk.chatapp.store.presentation.screen.main.imageViewer

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.jvziyaoyao.scale.zoomable.pager.DEFAULT_ITEM_SPACE
import com.jvziyaoyao.scale.zoomable.pager.PagerGestureScope
import com.jvziyaoyao.scale.zoomable.pager.PagerZoomablePolicyScope
import com.jvziyaoyao.scale.zoomable.pager.SupportedPagerState
import com.jvziyaoyao.scale.zoomable.pager.ZoomablePagerState
import com.jvziyaoyao.scale.zoomable.zoomable.ZoomableGestureScope
import com.jvziyaoyao.scale.zoomable.zoomable.ZoomableView
import com.jvziyaoyao.scale.zoomable.zoomable.rememberZoomableState
import kotlinx.coroutines.launch

//라이브러리와 foundation 버전 충돌로 인해 오류가 발생
//라이브러리 코드의 일부를 수정 커스텀
@Composable
fun CustomZoomablePager(
    modifier: Modifier = Modifier,
    state: ZoomablePagerState,
    itemSpacing: Dp = DEFAULT_ITEM_SPACE,
    userScrollEnabled: Boolean = true,
    detectGesture: PagerGestureScope = PagerGestureScope(),
    zoomablePolicy: @Composable PagerZoomablePolicyScope.(page: Int) -> Unit,
) {
    val scope = rememberCoroutineScope()
    // 确保不会越界
    CustomSupportedHorizonPager(
        state = state.pagerState,
        modifier = modifier
            .fillMaxSize(),
        itemSpacing = itemSpacing,
        userScrollEnabled = userScrollEnabled,
    ) { page ->
        Box(modifier = Modifier.fillMaxSize()) {
            PagerZoomablePolicyScope { intrinsicSize, content ->
                val zoomableState = rememberZoomableState(contentSize = intrinsicSize)
                LaunchedEffect(key1 = state.currentPage, key2 = zoomableState) {
                    if (state.currentPage == page) {
                        state.zoomableViewState.value = zoomableState
                    } else {
                        zoomableState.reset()
                    }
                }
                ZoomableView(
                    state = zoomableState,
                    boundClip = false,
                    detectGesture = ZoomableGestureScope(
                        onTap = { detectGesture.onTap() },
                        onDoubleTap = {
                            val consumed = detectGesture.onDoubleTap()
                            if (!consumed) scope.launch {
                                zoomableState.toggleScale(it)
                            }
                        },
                        onLongPress = { detectGesture.onLongPress() },
                    )
                ) {
                    content(zoomableState)
                }
            }.zoomablePolicy(page)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CustomSupportedHorizonPager(
    modifier: Modifier = Modifier,
    state: SupportedPagerState,
    itemSpacing: Dp = 0.dp,
    userScrollEnabled: Boolean = true,
//    flingBehavior: SnapFlingBehavior = PagerDefaults.flingBehavior(state = state.pagerState),
    content: @Composable (page: Int) -> Unit,
) {
    HorizontalPager(
        state = state.pagerState,
        modifier = modifier,
        pageSpacing = itemSpacing,
        userScrollEnabled = userScrollEnabled,
//        flingBehavior = flingBehavior
//        flingBehavior = defaultFlingBehavior(pagerState = state),
    ) { page ->
        content(page)
    }
}