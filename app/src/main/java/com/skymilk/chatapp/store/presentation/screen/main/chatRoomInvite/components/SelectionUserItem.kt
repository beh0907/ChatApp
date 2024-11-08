package com.skymilk.chatapp.store.presentation.screen.main.chatRoomInvite.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.skymilk.chatapp.R
import com.skymilk.chatapp.store.data.dto.User
import com.skymilk.chatapp.store.presentation.common.squircleClip
import com.skymilk.chatapp.ui.theme.dimens

@Composable
fun SelectionUserItem(
    user: User,
    isSelected: Boolean,
    onUserSelect: (User) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onUserSelect(user) }
            .padding(horizontal = 10.dp, vertical = MaterialTheme.dimens.small1),
        verticalAlignment = Alignment.CenterVertically
    ) {
        //이미지 정보
        AsyncImage(
            modifier = Modifier
                .size(50.dp)
                .squircleClip(),
            model = user.profileImageUrl.ifBlank { R.drawable.bg_default_profile },
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
            style = MaterialTheme.typography.bodyLarge
        )

        Checkbox(
            checked = isSelected,
            onCheckedChange = { onUserSelect(user) }
        )
    }
}