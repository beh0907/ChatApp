package com.skymilk.chatapp.store.presentation.common

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember

//스크롤마다 최하단인지 체크
@Composable
fun LazyListState.isAtEnd(isReverse: Boolean = false): Boolean {

    return remember(this) {
        derivedStateOf {
            val visibleItemsInfo = layoutInfo.visibleItemsInfo

            //기본적으로 리스트 사이즈가 0일땐 true
            if (layoutInfo.totalItemsCount == 0) {
                true
            } else {
                //reverse 설정 여부에 따라 다르게 설정
                if (isReverse) {
                    val firstVisibleItem =
                        visibleItemsInfo.firstOrNull() ?: return@derivedStateOf false
                    firstVisibleItem.index == 0 && firstVisibleItem.offset >= 0
                } else {
                    val lastVisibleItem =
                        visibleItemsInfo.lastOrNull() ?: return@derivedStateOf false
                    val viewportHeight =
                        layoutInfo.viewportEndOffset + layoutInfo.viewportStartOffset

                    (lastVisibleItem.index == layoutInfo.totalItemsCount - 1) &&
                            (lastVisibleItem.offset + lastVisibleItem.size <= viewportHeight)
                }


            }
        }
    }.value
}

@Composable
fun ScrollToEndCallback(scrollState: LazyListState, isReverse: Boolean = false, callback: () -> Unit) {
    val isAtBottom = scrollState.isAtEnd(isReverse)
    LaunchedEffect(isAtBottom) {
        if (isAtBottom) callback.invoke()
    }
}