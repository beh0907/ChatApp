package com.skymilk.chatapp.store.presentation.screen.main.userSearch

import com.skymilk.chatapp.store.domain.model.User

sealed class UserSearchState {
    data object Initial : UserSearchState()
    data object Loading : UserSearchState()
    data class Success(val users: List<User>) : UserSearchState()
    data class Error(val message: String) : UserSearchState()
}
