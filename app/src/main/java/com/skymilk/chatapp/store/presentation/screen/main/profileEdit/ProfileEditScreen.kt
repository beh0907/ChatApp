package com.skymilk.chatapp.store.presentation.screen.main.profileEdit

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.mr0xf00.easycrop.CropError
import com.mr0xf00.easycrop.CropResult
import com.mr0xf00.easycrop.CropperStyle
import com.mr0xf00.easycrop.ImageCropper
import com.mr0xf00.easycrop.crop
import com.mr0xf00.easycrop.rememberImageCropper
import com.mr0xf00.easycrop.ui.ImageCropperDialog
import com.skymilk.chatapp.store.domain.model.User
import com.skymilk.chatapp.store.presentation.screen.main.profile.ProfileEventSection
import com.skymilk.chatapp.ui.theme.HannaPro
import com.skymilk.chatapp.ui.theme.dimens
import gun0912.tedimagepicker.builder.TedImagePicker
import kotlinx.coroutines.launch

@Composable
fun ProfileEditScreen(
    modifier: Modifier,
    viewModel: ProfileEditViewModel,
    user: User,
    onNavigateToBack: () -> Unit
) {
    val context = LocalContext.current
    val imageCropper = rememberImageCropper()
    val cropState = imageCropper.cropState

    Box(modifier = modifier.fillMaxSize()) {

        TopSection(
            onNavigateToBack = { onNavigateToBack() },
            onSubmit = viewModel::updateUserProfile
        )

        //프로필 정보
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {
            EditProfileSection(context, imageCropper, user)

            HorizontalDivider()

            EditEventSection()
        }
    }

    if (cropState != null)
        ImageCropperDialog(
            state = cropState,
            style = CropperStyle(

            )
        )
}

@Composable
fun TopSection(
    onNavigateToBack: () -> Unit,
    onSubmit: (User) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        IconButton(onClick = {
            onNavigateToBack()
        }) {
            Icon(
                imageVector = Icons.AutoMirrored.Default.ArrowBack,
                contentDescription = null,
            )
        }

        Text(
            modifier = Modifier.weight(1f),
            text = "프로필 편집",
            fontFamily = HannaPro,
            style = MaterialTheme.typography.titleLarge,
        )

        TextButton(onClick = {
//            onSubmit()
        }) {
            Text(
                text = "완료",
                fontFamily = HannaPro,
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }
}


@Composable
private fun EditProfileSection(
    context: Context,
    imageCropper: ImageCropper,
    user: User
) {

    //프로필 이미지
    val scope = rememberCoroutineScope()
    var selectedImage by remember { mutableStateOf<ImageBitmap?>(null) }
    var error by remember { mutableStateOf<CropError?>(null) }

    Surface(
        shadowElevation = 4.dp,
        shape = RoundedCornerShape(30.dp),
        onClick = {
            //테드 이미지 픽커
            TedImagePicker.with(context)
                .start { uri ->
                    scope.launch {
                        when (val result = imageCropper.crop(uri = uri, context = context)) {
                            CropResult.Cancelled -> {}
                            is CropError -> error = result
                            is CropResult.Success -> {
                                selectedImage = result.bitmap
                            }
                        }
                    }
                }

        }
    ) {
        if (selectedImage != null) {
            Image(
                modifier = Modifier
                    .size(100.dp),
                bitmap = selectedImage!!,
                contentDescription = null,
                contentScale = ContentScale.Crop
            )
        } else {
            AsyncImage(
                modifier = Modifier
                    .size(100.dp),
                model = ImageRequest.Builder(context)
                    .data(user.profileImageUrl ?: "https://via.placeholder.com/150")
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop
            )
        }
    }

    Spacer(modifier = Modifier.height(MaterialTheme.dimens.small3))

    //유저 이름
    TextField(
        value = user.username,
        onValueChange = {
            user.username = it
        },
        placeholder = { Text("이름", fontFamily = HannaPro)}
    )

    Spacer(modifier = Modifier.height(MaterialTheme.dimens.small3))

    //상태 메시지
    TextField(
        value = user.statusMessage,
        onValueChange = {
            user.statusMessage = it
        },
        placeholder = { Text("상태 메시지", fontFamily = HannaPro)}
    )

    Spacer(modifier = Modifier.height(MaterialTheme.dimens.small3))
}

@Composable
fun EditEventSection(
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {

        //나와의 채팅
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .clickable {

                },
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.ChatBubble,
                contentDescription = null
            )

            Spacer(Modifier.height(10.dp))

            Text(
                text = "나와의 채팅",
                style = MaterialTheme.typography.titleSmall,
                maxLines = 1,
                fontFamily = HannaPro
            )
        }

        //프로필 편집
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .clickable {
                },
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = null
            )

            Spacer(Modifier.height(10.dp))

            Text(
                text = "프로필 편집",
                style = MaterialTheme.typography.titleSmall,
                maxLines = 1,
                fontFamily = HannaPro
            )
        }

        //로그아웃
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .clickable {
                },
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Default.Logout,
                contentDescription = null
            )

            Spacer(Modifier.height(10.dp))

            Text(
                text = "로그아웃",
                style = MaterialTheme.typography.titleSmall,
                maxLines = 1,
                fontFamily = HannaPro
            )
        }
    }
}