package com.skymilk.chatapp.store.presentation.common

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import com.skymilk.chatapp.BuildConfig
import com.skymilk.chatapp.utils.ImageUtil.createImageFile

@Composable
fun CameraCapture(
    onCameraCaptured: (Uri) -> Unit
) {
    val context = LocalContext.current
    val photoUri = remember { mutableStateOf<Uri?>(null) }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        photoUri.value?.let {
            if (success) {
                onCameraCaptured(it)
            }
        }
    }

    // 파일을 저장할 URI 생성
    val fileUri = remember {
        FileProvider.getUriForFile(
            context,
            BuildConfig.APPLICATION_ID + ".fileprovider",
            createImageFile(context)
        )
    }

    photoUri.value = fileUri
    launcher.launch(fileUri)
}