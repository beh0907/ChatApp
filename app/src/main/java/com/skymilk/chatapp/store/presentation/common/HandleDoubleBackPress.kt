package com.skymilk.chatapp.store.presentation.common

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
fun HandleDoubleBackPress() {
    val context = LocalContext.current
    var backPressedTime = 0L // 뒤로가기 버튼을 눌렀던 시간을 저장하는 변수

    BackHandler(enabled = true) {
        // 만약 전에 뒤로가기 버튼 누른 시간과 특정한 시간 만큼 차이가 나지 않으면 앱종료.
        if (System.currentTimeMillis() - backPressedTime <= 2000L) {
            (context as Activity).finish() // 앱 종료
        } else {
            // 특정한 시간 이상으로 차이가 난다면 토스트로 한 번 더 버튼을 누르라고 알림
            Toast.makeText(context, "한 번 더 누르면 앱이 종료됩니다.", Toast.LENGTH_SHORT).show()
        }
        // 뒤로가기 버튼을 눌렀던 시간을 저장
        backPressedTime = System.currentTimeMillis()
    }
}