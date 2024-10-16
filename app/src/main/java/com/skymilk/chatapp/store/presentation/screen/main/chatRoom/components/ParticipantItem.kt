package com.skymilk.chatapp.store.presentation.screen.main.chatRoom.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.skymilk.chatapp.R
import com.skymilk.chatapp.store.domain.model.User
import com.skymilk.chatapp.store.presentation.common.squircleClip
import com.skymilk.chatapp.ui.theme.dimens

@Composable
fun ParticipantItem(
    currentUser: User,
    participant: User,
    onUserItemClick: (User) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onUserItemClick(participant)
            }
            .padding(MaterialTheme.dimens.small2),
        verticalAlignment = Alignment.CenterVertically
    ) {
        //이미지 정보
        AsyncImage(
            modifier = Modifier
                .size(40.dp)
                .squircleClip(),
            model = ImageRequest.Builder(LocalContext.current)
                .data(
                    if (participant.profileImageUrl.isNullOrBlank()) R.drawable.bg_default_profile
                    else participant.profileImageUrl
                )
                .decoderFactory(SvgDecoder.Factory())
                .build(),
            contentScale = ContentScale.Crop,
            contentDescription = null,
        )

        if (currentUser.id == participant.id) {
            Spacer(Modifier.width(10.dp))

            Text(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(
                        MaterialTheme.colorScheme.primary
                    ),
                text = " 나 ",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.surface
            )
        }

        //유저 이름
        Text(
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .weight(1f),
            text = participant.username,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}