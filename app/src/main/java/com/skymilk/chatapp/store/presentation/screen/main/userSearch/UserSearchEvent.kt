package com.skymilk.chatapp.store.presentation.screen.main.userSearch

import com.skymilk.chatapp.store.domain.model.User

sealed interface UserSearchEvent {

    data class UserSearch(val query: String) : UserSearchEvent // 유저 검색

}
