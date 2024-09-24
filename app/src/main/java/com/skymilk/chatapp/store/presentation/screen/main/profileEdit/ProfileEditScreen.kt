package com.skymilk.chatapp.store.presentation.screen.main.profileEdit

import android.content.Context
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.mr0xf00.easycrop.CropResult
import com.mr0xf00.easycrop.CropperStyle
import com.mr0xf00.easycrop.ImageCropper
import com.mr0xf00.easycrop.crop
import com.mr0xf00.easycrop.rememberImageCropper
import com.mr0xf00.easycrop.ui.ImageCropperDialog
import com.skymilk.chatapp.store.domain.model.User
import com.skymilk.chatapp.store.presentation.common.CustomFullScreenEditDialog
import com.skymilk.chatapp.ui.theme.HannaPro
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

    //이미지 자르기
    val imageCropper = rememberImageCropper()
    val cropState = imageCropper.cropState

    //프로필 편집 정보
    var showEditDialog by remember { mutableStateOf(false) }
    var editingField by remember { mutableStateOf("") }
    var editName by remember { mutableStateOf(user.username) }
    var editStatusMessage by remember { mutableStateOf(user.statusMessage) }
    var selectedImage by remember { mutableStateOf<ImageBitmap?>(null) }

    Box(modifier = modifier.fillMaxSize()) {
        //편집 다이얼로그가 나타날 때는
        //타이틀이 겹치기 때문에 임시로 숨긴다
        TopSection(
            modifier = if (showEditDialog) Modifier.alpha(0f) else Modifier,
            onNavigateToBack = { onNavigateToBack() },
            onProfileUpdate = {
                viewModel.updateUserProfile(
                    userId = user.id,
                    name = editName,
                    statusMessage = editStatusMessage,
                    imageBitmap = selectedImage
                )
            }
        )

        //프로필 정보
        Column(
            modifier = modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {
            EditProfileSection(
                context = context,
                imageCropper = imageCropper,
                profileImageUrl = user.profileImageUrl,
                editName = editName,
                editStatusMessage = editStatusMessage,
                selectedImage = selectedImage,
                onNameClick = {
                    editingField = "name"
                    showEditDialog = true
                },
                onStatusClick = {
                    editingField = "status"
                    showEditDialog = true
                },
                onSelectedImage = { imageBitmap ->
                    selectedImage = imageBitmap
                }
            )

            HorizontalDivider(modifier = Modifier.padding(horizontal = 24.dp))

            EditEventSection()
        }
    }

    //이미지 크롭 다이얼로그
    if (cropState != null) {
        ImageCropperDialog(
            state = cropState,
            style = CropperStyle()
        )
    }

    //텍스트 편집 다이얼로그
    if (showEditDialog) {
        CustomFullScreenEditDialog(
            initText = if (editingField == "name") editName else editStatusMessage,
            onDismiss = { showEditDialog = false },
            onConfirm = { newText ->
                if (editingField == "name") editName = newText
                else editStatusMessage = newText

                showEditDialog = false
            }
        )
    }
}

@Composable
fun TopSection(
    modifier: Modifier = Modifier,
    onNavigateToBack: () -> Unit,
    onProfileUpdate: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
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
            onProfileUpdate()
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
    profileImageUrl: String?,
    editName: String,
    editStatusMessage: String,
    selectedImage:ImageBitmap?,
    onNameClick: () -> Unit,
    onStatusClick: () -> Unit,
    onSelectedImage: (ImageBitmap) -> Unit
) {
    val scope = rememberCoroutineScope()

    Surface(
        shadowElevation = 4.dp,
        shape = RoundedCornerShape(30.dp),
        onClick = {
            //테드 이미지 픽커
            TedImagePicker.with(context)
                .start { uri ->
                    scope.launch {
                        when (val result = imageCropper.crop(uri = uri, context = context)) {
                            is CropResult.Success -> {
                                onSelectedImage(result.bitmap)
                            }

                            else -> {}
                        }
                    }
                }
        }
    ) {

        //프로필 이미지
        if (selectedImage != null) {
            //Coil Image는 ImageBitmap을 적용시키지 못함
            Image(
                modifier = Modifier
                    .size(100.dp),
                bitmap = selectedImage,
                contentDescription = null,
                contentScale = ContentScale.Crop
            )
        } else {
            AsyncImage(
                modifier = Modifier
                    .size(100.dp),
                model = ImageRequest.Builder(context)
                    .data(profileImageUrl ?: "https://via.placeholder.com/150")
                    .crossfade(true)
                    .build(),
                contentScale = ContentScale.Crop,
                contentDescription = null
            )
        }
    }

    Spacer(modifier = Modifier.height(17.dp))

    //유저 이름
    Box(
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .fillMaxWidth()
            .clickable {
                onNameClick()
            }
    ) {

        Text(
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.titleLarge,
            text = editName,
            fontFamily = HannaPro,
            textAlign = TextAlign.Center
        )

        Icon(
            modifier = Modifier.align(Alignment.CenterEnd),
            imageVector = Icons.Default.Edit,
            contentDescription = null
        )

    }

    HorizontalDivider(
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .height(24.dp)
    )


    //상태 메시지
    Box(
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .fillMaxWidth()
            .clickable {
                onStatusClick()
            }
    ) {

        Text(
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.titleMedium,
            text = editStatusMessage,
            fontFamily = HannaPro,
            textAlign = TextAlign.Center
        )

        Icon(
            modifier = Modifier.align(Alignment.CenterEnd),
            imageVector = Icons.Default.Edit,
            contentDescription = null
        )
    }
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


    }
}