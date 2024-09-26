package com.skymilk.chatapp.store.presentation.screen.main.imageViewer

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import com.skymilk.chatapp.ui.theme.Black

@Composable
fun ImageViewerScreen(
    modifier: Modifier = Modifier,
    imageUrl: String,
    onNavigateToBack: () -> Unit,
) {
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

        TopSection(
            modifier = Modifier,
            onNavigateToBack = onNavigateToBack
        )
    }
}

@Composable
private fun TopSection(
    modifier: Modifier = Modifier,
    onNavigateToBack: () -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth()
    ) {
        IconButton(onClick = { onNavigateToBack() }) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = null,
                tint = Color.White
            )
        }
    }
}