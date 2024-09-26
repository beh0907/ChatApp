package com.skymilk.chatapp.store.presentation.screen.main.profile

sealed class FriendState {
    data object Initial : FriendState()
    data object Loading : FriendState()
    data class Success(val isFriend: Boolean) : FriendState()
    data object Error : FriendState()
}
