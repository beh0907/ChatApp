package com.skymilk.chatapp.store.presentation.screen.main.userSearch

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.skymilk.chatapp.store.domain.model.User
import com.skymilk.chatapp.store.presentation.common.EmptyScreen
import com.skymilk.chatapp.store.presentation.common.ErrorScreen
import com.skymilk.chatapp.ui.theme.CookieRunFont

@Composable
fun UserSearchScreen(
    modifier: Modifier = Modifier,
    currentUser: User,
    viewModel: UserSearchViewModel,
    onNavigateToProfile: (User) -> Unit,
    onNavigateToBack: () -> Unit
) {
    val userSearchState by viewModel.userSearchState.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
    ) {
        //상단바 타이틀
        TopSection(
            onNavigateToBack = onNavigateToBack
        )

        //검색 영역
        UserSearchSection(
            searchUser = viewModel::searchUser
        )

        when (userSearchState) {
            is UserSearchState.Initial -> {}
            is UserSearchState.Loading -> {
                UserSearchListShimmer()
            }

            is UserSearchState.Success -> {
                val users = (userSearchState as UserSearchState.Success).users

                if (users.isEmpty()) {
                    EmptyScreen("검색 결과가 없습니다.")
                    return
                }

                UserSearchList(
                    currentUser = currentUser,
                    users = users,
                    onUserItemClick = onNavigateToProfile
                )

            }

            is UserSearchState.Error -> {
                val message = (userSearchState as UserSearchState.Error).message

                ErrorScreen(message = message)
            }
        }
    }
}

@Composable
fun TopSection(
    modifier: Modifier = Modifier,
    onNavigateToBack: () -> Unit,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 뒤로가기 버튼
        IconButton(
            onClick = { onNavigateToBack() }
        ) {
            Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "뒤로 가기")
        }

        Text(
            modifier = Modifier.weight(1f),
            text = "유저 검색",
            fontFamily = CookieRunFont,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun UserSearchSection(
    modifier: Modifier = Modifier,
    searchUser: (String) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    var searchQuery by rememberSaveable { mutableStateOf("") }

    // 검색어 입력 칸
    TextField(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.primaryContainer),
        value = searchQuery,
        onValueChange = { searchQuery = it },
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onDone = {
            searchUser(searchQuery)
            keyboardController?.hide()
        }),
        singleLine = true,
        placeholder = {
            Text(
                text = "아이디 혹은 이름을 검색해주세요.",
                fontFamily = CookieRunFont,
                color = Color.Gray,
            )
        },
        trailingIcon = {
            // 검색 버튼
            IconButton(
                onClick = { searchUser(searchQuery) },
                enabled = searchQuery.isNotEmpty()
            ) {
                Icon(Icons.Rounded.Search, contentDescription = "검색")
            }
        },
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent
        ),
        textStyle = TextStyle(
            fontFamily = CookieRunFont,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
    )
}