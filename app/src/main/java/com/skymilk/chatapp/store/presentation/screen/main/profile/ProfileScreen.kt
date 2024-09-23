package com.skymilk.chatapp.store.presentation.screen.main.profile

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
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.skymilk.chatapp.store.domain.model.User
import com.skymilk.chatapp.store.presentation.common.CustomAlertDialog
import com.skymilk.chatapp.ui.theme.HannaPro
import com.skymilk.chatapp.ui.theme.dimens

@Composable
fun ProfileScreen(
    modifier: Modifier,
    user: User,
    onNavigateToBack: () -> Unit,
    onSignOut: () -> Unit,
    onNavigateToProfileEdit: () -> Unit
) {
    var visibleSignOutDialog by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize()) {
        //상단 아이콘
        TopSection(
            modifier = Modifier,
            onNavigateToBack = { onNavigateToBack() }
        )

        //프로필 정보
        Column(
            modifier = modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {
            UserProfileSection(user)

            HorizontalDivider()

            ProfileEventSection(
                onNavigateToProfileEdit = onNavigateToProfileEdit,
                visibleSignOutDialog = {
                    visibleSignOutDialog = true
                }
            )
        }
    }

    if (visibleSignOutDialog)
        CustomAlertDialog(
            message = "로그아웃 하시겠습니까?",
            onConfirm = { onSignOut() },
            onDismiss = { visibleSignOutDialog = false })
}

@Composable
private fun TopSection(
    modifier: Modifier = Modifier,
    onNavigateToBack: () -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth()
    ) {
        IconButton(onClick = { onNavigateToBack() }) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = null
            )
        }
    }
}

@Composable
fun UserProfileSection(
    user: User,
) {
    val context = LocalContext.current

    //프로필 이미지
    Surface(
        shadowElevation = 4.dp,
        shape = RoundedCornerShape(30.dp)
    ) {
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

    Spacer(modifier = Modifier.height(17.dp))

    //유저 이름
    Text(
        text = user.username,
        style = MaterialTheme.typography.titleLarge,
        fontFamily = HannaPro,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )

    Spacer(modifier = Modifier.height(24.dp))

    //유저 상대 메시지
    Text(
        text = user.statusMessage,
        style = MaterialTheme.typography.titleMedium,
        fontFamily = HannaPro,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )

    Spacer(modifier = Modifier.height(10.dp))
}

@Composable
fun ProfileEventSection(
    onNavigateToProfileEdit: () -> Unit,
    visibleSignOutDialog: () -> Unit
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

                }
                .padding(horizontal = 10.dp),
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
                    onNavigateToProfileEdit()
                }
                .padding(horizontal = 10.dp),
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
                    visibleSignOutDialog()
                }
                .padding(horizontal = 10.dp),
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
