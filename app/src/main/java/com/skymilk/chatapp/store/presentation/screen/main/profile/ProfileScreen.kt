package com.skymilk.chatapp.store.presentation.screen.main.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.mr0xf00.easycrop.CropError
import com.mr0xf00.easycrop.CropResult
import com.mr0xf00.easycrop.CropperStyle
import com.mr0xf00.easycrop.crop
import com.mr0xf00.easycrop.rememberImageCropper
import com.mr0xf00.easycrop.rememberImagePicker
import com.mr0xf00.easycrop.ui.ImageCropperDialog
import com.skymilk.chatapp.store.domain.model.User
import com.skymilk.chatapp.ui.theme.dimens
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    modifier: Modifier,
    viewModel: ProfileViewModel,
    currentUser: User?,
    onSignOut: () -> Unit
) {
    val imageCropper = rememberImageCropper()
    val cropState = imageCropper.cropState
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var selectedImage by remember { mutableStateOf<ImageBitmap?>(null) }
    var error by remember { mutableStateOf<CropError?>(null) }
    val imagePicker = rememberImagePicker(onImage = { uri ->
        scope.launch {
            when (val result = imageCropper.crop(uri = uri, context = context)) {
                CropResult.Cancelled -> {}
                is CropError -> error = result
                is CropResult.Success -> {
                    selectedImage = result.bitmap
                }
            }
        }
    })

    Box(modifier = modifier.fillMaxSize()) {
        //상단 아이콘
        IconButton(
            modifier = Modifier
                .padding(10.dp)
                .align(Alignment.TopEnd),
            onClick = { onSignOut() }) {
            Icon(
                imageVector = Icons.AutoMirrored.Default.Logout,
                contentDescription = null,
                Modifier.size(36.dp)
            )
        }


        //프로필 정보
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {
            currentUser.let { user ->
                //프로필 이미지
                user?.profileImageUrl?.let {

                    Surface(
                        shadowElevation = 4.dp,
                        shape = RoundedCornerShape(30.dp),
                        onClick = {
                            imagePicker.pick()
                        }
                    ) {
                        if (selectedImage != null) {
                            Image(
                                modifier = Modifier
                                    .size(100.dp),
                                bitmap = selectedImage!!,
                                contentDescription = null,
                            )
                        } else {
                            AsyncImage(
                                modifier = Modifier
                                    .size(100.dp),
                                model = ImageRequest.Builder(context)
                                    .data(it)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = null,
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(MaterialTheme.dimens.small3))
                }

                //유저 이름
                user?.username?.let { name ->
                    Text(
                        text = name,
                        style = MaterialTheme.typography.titleLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(MaterialTheme.dimens.small3))
                }
            }
        }
    }

    if (cropState != null)
        ImageCropperDialog(
            state = cropState,
            style = CropperStyle(

            )
        )
}