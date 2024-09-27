package com.skymilk.chatapp.store.presentation.screen.main.imageViewer

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import coil.compose.rememberAsyncImagePainter
import com.jvziyaoyao.scale.zoomable.zoomable.ZoomableView
import com.jvziyaoyao.scale.zoomable.zoomable.rememberZoomableState

@Composable
fun ImageViewerScreen(
    modifier: Modifier = Modifier,
    imageUrl: String,
    onNavigateToBack: () -> Unit,
) {
//    val images = remember {
//        mutableStateListOf(
//            "https://t7.baidu.com/it/u=1595072465,3644073269&fm=193&f=GIF",
//            "https://t7.baidu.com/it/u=4198287529,2774471735&fm=193&f=GIF",
//            imageUrl
//        )
//    }
//    val pagerState = rememberZoomablePagerState { images.size }

    val painter = rememberAsyncImagePainter(imageUrl)
    val state = rememberZoomableState(contentSize = painter.intrinsicSize)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.onSurface)
    ) {

        ZoomableView(state = state) {
            Image(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.onSurface),
                painter = painter,
                contentDescription = null,
            )
        }

//        ZoomablePager(state = pagerState) { page ->
//            val imageRequest = ImageRequest.Builder(LocalContext.current)
//                .data(images[page])
//                .size(coil.size.Size.ORIGINAL)
//                .build()
//            val painter = rememberAsyncImagePainter(imageRequest)
//
//            if (painter.intrinsicSize.isSpecified) {
//                ZoomablePolicy(intrinsicSize = painter.intrinsicSize) { _ ->
//                    Image(
//                        modifier = Modifier.fillMaxSize(),
//                        painter = painter,
//                        contentDescription = null
//                    )
//                }
//
//                if (!painter.intrinsicSize.isSpecified) {
//                    Box(modifier = Modifier.fillMaxSize()) {
//                        CircularProgressIndicator(
//                            modifier = Modifier.align(Alignment.Center)
//                        )
//                    }
//                }
//            }
//        }

        TopSection(
            modifier = Modifier, onNavigateToBack = onNavigateToBack
        )
    }
}

@Composable
private fun TopSection(
    modifier: Modifier = Modifier, onNavigateToBack: () -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth()
    ) {
        IconButton(onClick = { onNavigateToBack() }) {
            Icon(
                imageVector = Icons.Default.Close, contentDescription = null, tint = Color.White
            )
        }
    }
}