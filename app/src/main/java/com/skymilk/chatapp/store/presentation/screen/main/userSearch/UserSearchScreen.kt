package com.skymilk.chatapp.store.presentation.screen.main.userSearch

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.skymilk.chatapp.store.domain.model.User
import com.skymilk.chatapp.ui.theme.HannaPro

@Composable
fun UserSearchScreen(
    modifier: Modifier = Modifier,
    currentUser: User,
    viewModel: UserSearchViewModel,
    onNavigateToBack: () -> Boolean,
    onNavigateToProfile: (User) -> Unit
) {
    val userSearchState by viewModel.userSearchState.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
    ) {
        TopSection(
            onNavigateToBack = {},
            searchUser = viewModel::searchUser
        )

        when (userSearchState) {
            is UserSearchState.Initial -> {}
            is UserSearchState.Loading -> {
                UserSearchListShimmer()
            }

            is UserSearchState.Success -> {
                val users = (userSearchState as UserSearchState.Success).users

                UserSearchList(
                    users = users,
                    onUserItemClick = onNavigateToProfile
                )

            }
            is UserSearchState.Error -> {}
        }
    }
}

@Composable
private fun TopSection(
    modifier: Modifier = Modifier,
    onNavigateToBack: () -> Unit,
    searchUser: (String, String) -> Unit
) {
    var searchQuery by rememberSaveable { mutableStateOf("") }

    TextField(
        value = searchQuery,
        onValueChange = { newValue ->
            searchQuery = newValue
        },
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        placeholder = { Text("검색어를 입력하세요", fontFamily = HannaPro) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search Icon"
            )
        },
        colors = TextFieldDefaults.colors(
//            containerColor = MaterialTheme.colorScheme.surface
        ),
        singleLine = true,
        textStyle = TextStyle(
            fontFamily = HannaPro
        )
    )
}