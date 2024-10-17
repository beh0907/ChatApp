package com.skymilk.chatapp.store.presentation.screen.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.skymilk.chatapp.R

@Composable
fun SplashScreen(onAnimationFinished: () -> Unit) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.splash_lottie))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = 1, // 애니메이션을 한 번만 실행
    )

    //애니메이션 완료 시 종료
    if (progress == 1f) {
        LaunchedEffect(Unit) { onAnimationFinished() }
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
    ) {
        LottieAnimation(
            modifier = Modifier.fillMaxSize(),
            composition = composition,
            progress = { progress }
        )
    }
}