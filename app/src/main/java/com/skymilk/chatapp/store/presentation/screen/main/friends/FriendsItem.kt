package com.skymilk.chatapp.store.presentation.screen.main.friends

import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.skymilk.chatapp.store.domain.model.User
import com.skymilk.chatapp.store.presentation.common.shimmerEffect
import com.skymilk.chatapp.ui.theme.Black
import com.skymilk.chatapp.ui.theme.HannaPro
import com.skymilk.chatapp.ui.theme.dimens

@Composable
fun FriendsItem(
    user: User,
    onUserItemClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onUserItemClick()
            }
            .padding(MaterialTheme.dimens.small2),
    ) {
        //이미지 정보
        AsyncImage(
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(4.dp)),
            model = ImageRequest.Builder(
                LocalContext.current
            )
                .data(user.profileImageUrl)
                .crossfade(true)
                .build(),
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
                fontStyle = MaterialTheme.typography.bodyLarge.fontStyle,
                fontFamily = HannaPro
            )

            //유저 상태 메시지가 있다면
            if (user.statusMessage.isNotBlank()) {
                //유저 이름
                Text(
                    text = user.statusMessage,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontStyle = MaterialTheme.typography.bodyLarge.fontStyle,
                    fontFamily = HannaPro,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun FriendsItemShimmer() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(MaterialTheme.dimens.small2)
    ) {
        // 이미지 정보
        Box(
            modifier = Modifier
                .size(60.dp)
                .shimmerEffect()
                .clip(RoundedCornerShape(4.dp))
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