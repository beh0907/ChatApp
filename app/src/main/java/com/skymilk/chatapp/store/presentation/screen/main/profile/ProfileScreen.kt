package com.skymilk.chatapp.store.presentation.screen.main.profile

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.commit451.coiltransformations.BlurTransformation
import com.skymilk.chatapp.store.domain.model.User
import com.skymilk.chatapp.ui.theme.dimens

@Composable
fun ProfileScreen(
    modifier: Modifier,
    viewModel: ProfileViewModel,
    currentUser: User?,
    onSignOut: () -> Unit
) {
    val context = LocalContext.current

    Box(modifier = modifier.fillMaxSize()) {

        //전체 배경 화면
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(currentUser?.profileImageUrl)
                .transformations(BlurTransformation(context)) // 블러 반경 설정
                .build(),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop // 이미지의 크기 조정
        )


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
                        shape = RoundedCornerShape(30.dp)
                    ) {
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
}