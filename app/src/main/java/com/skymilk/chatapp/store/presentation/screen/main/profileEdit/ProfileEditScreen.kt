package com.skymilk.chatapp.store.presentation.screen.main.profileEdit

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import coil.util.CoilUtils.result
import com.mr0xf00.easycrop.CropResult
import com.mr0xf00.easycrop.CropperStyle
import com.mr0xf00.easycrop.ImageCropper
import com.mr0xf00.easycrop.crop
import com.mr0xf00.easycrop.rememberImageCropper
import com.mr0xf00.easycrop.ui.ImageCropperDialog
import com.skymilk.chatapp.R
import com.skymilk.chatapp.store.domain.model.User
import com.skymilk.chatapp.store.presentation.common.CustomFullScreenEditDialog
import com.skymilk.chatapp.store.presentation.common.CustomProgressDialog
import com.skymilk.chatapp.store.presentation.common.squircleClip
import com.skymilk.chatapp.store.presentation.utils.FileSizeUtil
import gun0912.tedimagepicker.builder.TedImagePicker
import kotlinx.coroutines.launch

@Composable
fun ProfileEditScreen(
    modifier: Modifier = Modifier,
    viewModel: ProfileEditViewModel,
    onEvent: (ProfileEditEvent) -> Unit,
    user: User,
    onNavigateToBack: () -> Unit
) {
    val profileEditState by viewModel.profileEditState.collectAsStateWithLifecycle()

    //이미지 자르기
    val imageCropper = rememberImageCropper()
    val cropState = imageCropper.cropState

    //프로필 편집 정보
    var showEditDialog by rememberSaveable { mutableStateOf(false) }
    var editingField by remember { mutableStateOf("") }
    var editName by remember { mutableStateOf(user.username) }
    var editStatusMessage by remember { mutableStateOf(user.statusMessage) }
    var selectedImage by remember { mutableStateOf<ProfileImage>(ProfileImage.Initial) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.secondary)
    ) {
        //편집 다이얼로그가 나타날 때는
        //타이틀이 겹치기 때문에 임시로 숨긴다
        TopSection(
            modifier = if (showEditDialog) Modifier.alpha(0f) else Modifier,
            onNavigateToBack = { onNavigateToBack() },
            onProfileUpdate = {
                onEvent(
                    ProfileEditEvent.UpdateUserProfile(
                        userId = user.id,
                        name = editName,
                        statusMessage = editStatusMessage,
                        profileImage = selectedImage
                    )
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

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 24.dp),
                color = Color.White
            )

            EditEventSection()
        }
    }

    //이미지 업데이트 로딩 표시
    if (profileEditState is ProfileEditState.Loading) {
        CustomProgressDialog("프로필을 업데이트 하고 있습니다.")
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
            maxLength = if (editingField == "name") 20 else 60,
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
                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                contentDescription = null,
                tint = Color.White
            )
        }

        Text(
            modifier = Modifier.weight(1f),
            text = "프로필 편집",
            style = MaterialTheme.typography.titleLarge,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )

        TextButton(onClick = {
            onProfileUpdate()
        }) {
            Text(
                text = "완료",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}


@Composable
private fun EditProfileSection(
    imageCropper: ImageCropper,
    profileImageUrl: String?,
    editName: String,
    editStatusMessage: String,
    selectedImage: ProfileImage,
    onNameClick: () -> Unit,
    onStatusClick: () -> Unit,
    onSelectedImage: (ProfileImage) -> Unit
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var showDropdownMenu by rememberSaveable { mutableStateOf(false) }

    Box {
        //프로필 이미지
        when (selectedImage) {
            is ProfileImage.Custom -> {
                //Coil Image는 ImageBitmap을 적용시키지 못함
                Image(
                    modifier = Modifier
                        .size(120.dp)
                        .squircleClip()
                        .shadow(4.dp),
                    bitmap = selectedImage.imageBitmap,
                    contentDescription = null,
                    contentScale = ContentScale.Crop
                )
            }

            else -> {
                AsyncImage(
                    modifier = Modifier
                        .size(120.dp)
                        .squircleClip()
                        .shadow(4.dp),
                    model = ImageRequest.Builder(context)
                        .data(
                            //이미지 url 정보가 없거나 기본 이미지 상태라면 기본 이미지 표시
                            if (profileImageUrl.isNullOrBlank() || selectedImage is ProfileImage.Default) R.drawable.bg_default_profile
                            else profileImageUrl
                        )
                        .decoderFactory(SvgDecoder.Factory())
                        .build(),
                    contentScale = ContentScale.Crop,
                    contentDescription = null
                )
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
        ) {
            SmallFloatingActionButton(
                modifier = Modifier.size(30.dp),
                containerColor = Color.White,
                contentColor = Color.Black,
                onClick = { showDropdownMenu = true }
            ) {
                Icon(
                    imageVector = Icons.Rounded.CameraAlt,
                    contentDescription = null,
                )
            }

            DropdownMenu(
                expanded = showDropdownMenu,
                onDismissRequest = { showDropdownMenu = false }
            ) {
                DropdownMenuItem(
                    text = { Text("기본 이미지 선택") },
                    onClick = {
                        //드롭다운 메뉴 닫기
                        showDropdownMenu = false

                        onSelectedImage(ProfileImage.Default)
                    }
                )

                DropdownMenuItem(
                    text = { Text("앨범 사진/카메라 선택") },
                    onClick = {
                        showDropdownMenu = false
                        TedImagePicker
                            .with(context)
                            .start { uri ->
                                //선택한 이미지 자르기
                                scope.launch {
                                    val result = imageCropper.crop(uri = uri, context = context)
                                    when (result) {
                                        is CropResult.Success -> {
                                            //이미지 리사이징 후 전달
                                            onSelectedImage(ProfileImage.Custom(FileSizeUtil.resizeImageBitmap(result.bitmap)))
                                        }

                                        else -> {}
                                    }
                                }
                            }
                    }
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    //유저 이름
    Box(
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .fillMaxWidth()
            .clickable(
                //리플 효과 제거
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                onNameClick()
            }
    ) {

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            style = MaterialTheme.typography.titleLarge,
            text = editName,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = Color.White
        )

        Icon(
            modifier = Modifier.align(Alignment.CenterEnd),
            imageVector = Icons.Rounded.Edit,
            contentDescription = null,
            tint = Color.White
        )

    }

    Spacer(modifier = Modifier.height(10.dp))

    HorizontalDivider(
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .height(24.dp),
        color = Color.White
    )


    //상태 메시지
    Box(
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .fillMaxWidth()
            .clickable(
                //리플 효과 제거
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                onStatusClick()
            }
    ) {

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            style = MaterialTheme.typography.titleMedium,
            text = editStatusMessage,
            textAlign = TextAlign.Center,
            color = Color.White
        )

        Icon(
            modifier = Modifier.align(Alignment.CenterEnd),
            imageVector = Icons.Rounded.Edit,
            contentDescription = null,
            tint = Color.White
        )
    }

    Spacer(modifier = Modifier.height(16.dp))
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
    ) {}
}