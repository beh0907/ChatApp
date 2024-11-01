package com.skymilk.chatapp.store.presentation.screen.main.chatRoom.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.skymilk.chatapp.store.data.dto.User

@Composable
fun ParticipantList(
    modifier: Modifier = Modifier,
    currentUser: User,
    participants: List<User>,
    onUserItemClick: (User) -> Unit,
    onNavigateToInviteFriends: () -> Unit
) {
    LazyColumn(modifier = modifier) {
        //헤더 정보
        item {
            Text(
                modifier = Modifier.padding(start = 15.dp),
                text = "참여자 ${participants.size}",
                style = MaterialTheme.typography.titleLarge,
            )
        }

        //참여자 추가
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToInviteFriends() }
                    .padding(15.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier
                        .padding(end = 10.dp)
                        .border(
                            BorderStroke(2.dp, MaterialTheme.colorScheme.onSurface),
                            shape = CircleShape
                        ),
                    imageVector = Icons.TwoTone.Add,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = "대화상대 초대",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }


        //참여자 목록
        items(participants, key = { participant -> participant.id }) { participant ->
            ParticipantItem(
                currentUser = currentUser,
                participant = participant,
                onUserItemClick = onUserItemClick
            )
        }
    }
}