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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.skymilk.chatapp.store.domain.model.User
import com.skymilk.chatapp.store.presentation.common.EmptyScreen
import com.skymilk.chatapp.store.presentation.common.ErrorScreen
import com.skymilk.chatapp.ui.theme.SamsungOneFont

@Composable
fun FriendsScreen(
    modifier: Modifier = Modifier,
    currentUser: User,
    viewModel: FriendsViewModel,
    onNavigateToProfile: (User) -> Unit,
    onNavigateToUserSearch: () -> Unit
) {
    val friendsState by viewModel.friendsState.collectAsStateWithLifecycle()

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        //헤더
        TopSection(onNavigateToUserSearch = onNavigateToUserSearch)

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

                if (friends.isEmpty()) {
                    EmptyScreen("등록된 친구가 없습니다.")
                    return
                }

                FriendsList(friends, onUserItemClick = { user ->
                    onNavigateToProfile(user)
                })
            }

            is FriendsState.Error -> {
                val message = (friendsState as FriendsState.Error).message

                ErrorScreen(message = message, retry = viewModel::loadFriends)
            }
        }
    }
}

//상단 타이틀
@Composable
fun TopSection(onNavigateToUserSearch: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = "친구",
            fontFamily = SamsungOneFont,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold
        )

        IconButton(onClick = {
            onNavigateToUserSearch()
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