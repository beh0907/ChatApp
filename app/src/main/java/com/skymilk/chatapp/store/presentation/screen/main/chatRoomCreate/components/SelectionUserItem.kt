package com.skymilk.chatapp.store.presentation.screen.main.chatRoomCreate.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.skymilk.chatapp.store.domain.model.User
import com.skymilk.chatapp.ui.theme.CookieRunFont
import com.skymilk.chatapp.ui.theme.dimens

@Composable
fun SelectionUserItem(
    user: User,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onSelect()
            }
            .padding(horizontal = 10.dp, vertical = MaterialTheme.dimens.small1),
        verticalAlignment = Alignment.CenterVertically
    ) {
        //이미지 정보
        AsyncImage(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape),
            model = user.profileImageUrl,
            contentScale = ContentScale.Crop,
            contentDescription = null,
        )

        //유저 이름
        Text(
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .weight(1f),
            text = user.username,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyLarge,
            fontFamily = CookieRunFont
        )

        Checkbox(
            checked = isSelected,
            onCheckedChange = { onSelect() }
        )
    }
}