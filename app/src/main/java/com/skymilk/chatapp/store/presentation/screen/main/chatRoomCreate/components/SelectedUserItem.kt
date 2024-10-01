package com.skymilk.chatapp.store.presentation.screen.main.chatRoomCreate.components

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import coil.compose.AsyncImage
import com.skymilk.chatapp.store.domain.model.User
import com.skymilk.chatapp.ui.theme.LeeSeoYunFont

@Composable
fun SelectedUserItem(
    user: User,
    onUserRemove: (User) -> Unit
) {

    ConstraintLayout {
        val (image, removeButton, username) = createRefs()

        AsyncImage(
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(10.dp))
                .constrainAs(image) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
            model = user.profileImageUrl ?: "https://via.placeholder.com/150",
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
            fontFamily = LeeSeoYunFont,
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.constrainAs(username) {
                top.linkTo(image.bottom, margin = 4.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                width = Dimension.preferredWrapContent
            }
        )
    }

//    Column(
//        modifier = Modifier.padding(8.dp),
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        AsyncImage(
//            modifier = Modifier
//                .size(50.dp)
//                .clip(RoundedCornerShape(10.dp)),
//            model = user.profileImageUrl ?: "https://via.placeholder.com/150",
//            contentScale = ContentScale.Crop,
//            contentDescription = null
//        )
//
//        Spacer(modifier = Modifier.height(4.dp))
//
//        Text(
//            text = user.username,
//            fontFamily = LeeSeoYunFont,
//            style = MaterialTheme.typography.titleMedium,
//        )
//    }
}