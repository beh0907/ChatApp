package com.skymilk.chatapp.store.presentation.screen.main.chatRoom.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.skymilk.chatapp.store.domain.model.User
import com.skymilk.chatapp.ui.theme.CookieRunFont

@Composable
fun ParticipantList(
    modifier:Modifier = Modifier,
    currentUser: User,
    participants: List<User>,
    onUserItemClick: (User) -> Unit,
) {
    LazyColumn(modifier = modifier) {
        item {
            Text(
                modifier = Modifier.padding(start = 15.dp),
                text = "참여자 ${participants.size}",
                style = MaterialTheme.typography.titleMedium,
                fontFamily = CookieRunFont
            )
        }

        items(participants, key = { participant -> participant.id }) { participant ->

            ParticipantItem(currentUser = currentUser, participant = participant, onUserItemClick = onUserItemClick)
        }
    }
}