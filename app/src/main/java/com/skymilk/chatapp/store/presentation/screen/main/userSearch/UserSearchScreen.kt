package com.skymilk.chatapp.store.presentation.screen.main.userSearch

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
    onNavigateToProfile: (User) -> Unit
) {
    val userSearchState by viewModel.userSearchState.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
    ) {
        TopSection(
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
                    currentUser = currentUser,
                    users = users,
                    onUserItemClick = onNavigateToProfile
                )

            }

            is UserSearchState.Error -> {}
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopSection(
    modifier: Modifier = Modifier,
    searchUser: (String, String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf(Pair("이름", "username")) }
    var isDropdownExpanded by remember { mutableStateOf(false) }

    val filterOptions = listOf(Pair("이름", "username"), Pair("아이디", "id")) // 필터 옵션 예시

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 드롭다운 메뉴 (필터)
        ExposedDropdownMenuBox(
            expanded = isDropdownExpanded,
            onExpandedChange = { isDropdownExpanded = it },
        ) {
            TextField(
                singleLine = true,
                value = selectedFilter.first,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDropdownExpanded) },
                modifier = Modifier.width(100.dp).menuAnchor(type, enabled),
                textStyle = TextStyle(
                    fontFamily = HannaPro
                )
            )
            ExposedDropdownMenu(
                expanded = isDropdownExpanded,
                onDismissRequest = { isDropdownExpanded = false }
            ) {
                filterOptions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option.first, fontFamily = HannaPro) },
                        onClick = {
                            selectedFilter = option
                            isDropdownExpanded = false
                        },

                        )
                }
            }
        }


        // 검색어 입력 칸
        TextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.weight(1f),
            singleLine = true,
            placeholder = { Text("검색어를 입력하세요", fontFamily = HannaPro) },
            trailingIcon = {
                // 검색 버튼
                IconButton(
                    onClick = { searchUser(searchQuery, selectedFilter.second) },
                    enabled = searchQuery.isNotEmpty()
                ) {
                    Icon(Icons.Default.Search, contentDescription = "검색")
                }
            },
            textStyle = TextStyle(
                fontFamily = HannaPro
            )
        )
    }
}