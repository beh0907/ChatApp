package com.skymilk.chatapp.store.presentation.screen.main.friends

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PersonAddAlt1
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.skymilk.chatapp.store.domain.model.User
import com.skymilk.chatapp.ui.theme.HannaPro

@Composable
fun FriendsScreen(
    modifier: Modifier = Modifier,
    currentUser: User,
    viewModel: FriendsViewModel,
    onNavigateToProfile: (User) -> Unit
) {
    val friendsState by viewModel.friendsState.collectAsStateWithLifecycle()


    Column(
        modifier = modifier.fillMaxSize()
    ) {
        //헤더
        TopSection()

        //내 정보
        FriendsItem(user = currentUser, onUserItemClick = {
            onNavigateToProfile(currentUser)
        })

        HorizontalDivider(modifier = Modifier.padding(horizontal = 10.dp))

        when (friendsState) {
            is FriendsState.Initial -> {}
            is FriendsState.Loading -> {
                FriendsListShimmer()
            }

            is FriendsState.Success -> {
                val friends = (friendsState as FriendsState.Success).friends

                Text(
                    modifier = Modifier.padding(top = 10.dp, start = 15.dp),
                    text = "친구 ${friends.size}",
                    fontFamily = HannaPro,
                    fontSize = 14.sp
                )

                FriendsList(friends, onUserItemClick = { user ->
                    onNavigateToProfile(user)
                })
            }
        }
    }


}

//상단 타이틀
@Composable
fun TopSection() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = "친구",
            fontFamily = HannaPro,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface
        )

        IconButton(onClick = {

        }) {
            Icon(
                imageVector = Icons.Outlined.PersonAddAlt1,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }

    HorizontalDivider()
}