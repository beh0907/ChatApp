package com.skymilk.chatapp.store.presentation.screen.main.friends

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.skymilk.chatapp.R
import com.skymilk.chatapp.store.data.dto.User
import com.skymilk.chatapp.store.presentation.common.shimmerEffect
import com.skymilk.chatapp.store.presentation.common.squircleClip
import com.skymilk.chatapp.ui.theme.dimens

@Composable
fun FriendsItem(
    user: User,
    profileSize: Dp = 50.dp,
    onUserItemClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onUserItemClick()
            }
            .padding(MaterialTheme.dimens.small1),
    ) {
        //이미지 정보
        AsyncImage(
            modifier = Modifier
                .size(profileSize)
                .squircleClip(),
            model = user.profileImageUrl.ifBlank { R.drawable.bg_default_profile },
            contentScale = ContentScale.Crop,
            contentDescription = null,
        )

        //유저 정보
        Column(
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .weight(1f),
            verticalArrangement = Arrangement.Center
        ) {

            //유저 이름
            Text(
                text = user.username,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(5.dp))

            //유저 상태 메시지가 있다면
            if (user.statusMessage.isNotBlank()) {
                Text(
                    text = user.statusMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun FriendsItemShimmer(
    profileSize: Dp = 50.dp
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(MaterialTheme.dimens.small2)
    ) {
        // 이미지 정보
        Box(
            modifier = Modifier
                .size(profileSize)
                .squircleClip()
                .shimmerEffect()
        )

        // 유저 정보
        Column(
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .weight(1f)
        ) {
            // 사용자 이름 자리 표시자
            Box(
                modifier = Modifier
                    .height(20.dp)
                    .shimmerEffect()
                    .fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 상태 메시지 자리 표시자
            Box(
                modifier = Modifier
                    .height(20.dp)
                    .shimmerEffect()
                    .fillMaxWidth()
            )
        }
    }
}