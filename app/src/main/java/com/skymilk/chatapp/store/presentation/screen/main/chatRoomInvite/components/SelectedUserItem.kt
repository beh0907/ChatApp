package com.skymilk.chatapp.store.presentation.screen.main.chatRoomInvite.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.skymilk.chatapp.R
import com.skymilk.chatapp.store.data.dto.User
import com.skymilk.chatapp.store.presentation.common.squircleClip

@Composable
fun SelectedUserItem(
    user: User,
    onUserRemove: (User) -> Unit
) {

    ConstraintLayout {
        val (image, removeButton, username) = createRefs()

        AsyncImage(
            modifier = Modifier
                .size(50.dp)
                .squircleClip()
                .constrainAs(image) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
            model = user.profileImageUrl.ifBlank { R.drawable.bg_default_profile },
            contentScale = ContentScale.Crop,
            contentDescription = null
        )

        IconButton(
            onClick = { onUserRemove(user) },
            modifier = Modifier
                .size(20.dp)
                .constrainAs(removeButton) {
                    top.linkTo(image.top)
                    bottom.linkTo(image.top)
                    start.linkTo(image.end)
                    end.linkTo(image.end)
                }
        ) {
            Icon(
                imageVector = Icons.Filled.Cancel,
                contentDescription = "Delete",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }

        Text(
            text = user.username,
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.constrainAs(username) {
                top.linkTo(image.bottom, margin = 4.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                width = Dimension.preferredWrapContent
            }
        )
    }
}