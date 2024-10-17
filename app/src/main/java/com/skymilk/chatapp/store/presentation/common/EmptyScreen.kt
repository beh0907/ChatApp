package com.skymilk.chatapp.store.presentation.common

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color.Companion.DarkGray
import androidx.compose.ui.graphics.Color.Companion.LightGray
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.skymilk.chatapp.R
import com.skymilk.chatapp.ui.theme.isAppInDarkTheme

@Composable
fun EmptyScreen(message: String, icon: Int = R.drawable.ic_error) {
    var startAnimation by remember {
        mutableStateOf(false)
    }

    val alphaAnimation by animateFloatAsState(
        targetValue = if (startAnimation) 0.3f else 0f,
        animationSpec = tween(durationMillis = 1000)
    )

    LaunchedEffect(key1 = true) {
        startAnimation = true
    }


    EmptyContent(alphaAnim = alphaAnimation, message = message, icon = icon)
}


@Composable
fun EmptyContent(alphaAnim: Float, message: String, icon: Int) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = null,
            tint = if (isAppInDarkTheme()) LightGray else DarkGray,
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .alpha(alphaAnim)
        )
        Text(
            modifier = Modifier
                .padding(20.dp)
                .alpha(alphaAnim),
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = if (isAppInDarkTheme()) LightGray else DarkGray,
        )
    }
}


@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun EmptyScreenPreview() {
    ErrorContent(alphaAnim = 0.3f, message = "검색된 결과가 없습니다.", R.drawable.ic_error)
}