package com.skymilk.chatapp.store.presentation.screen.main.profileImageViewer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import coil.compose.SubcomposeAsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.skymilk.chatapp.R
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable

@Composable
fun ProfileImageViewerScreen(
    modifier: Modifier = Modifier,
    imageUrl: String, // 이미지 URL
    onNavigateToBack: () -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        SubcomposeAsyncImage(
            modifier = Modifier
                .fillMaxSize()
                .zoomable(
                    zoomState = rememberZoomState(),
                ),
            model = imageUrl.ifBlank { R.drawable.bg_default_profile },
            loading = {
                Box(modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            },
            contentDescription = null,
        )

        //상단 메뉴
        TopSection(
            modifier = Modifier, onNavigateToBack = onNavigateToBack
        )
    }
}

@Composable
fun TopSection(
    modifier: Modifier = Modifier,
    onNavigateToBack: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
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