package com.skymilk.chatapp.store.presentation.screen.main.profileEdit

sealed class EditProfileState {
    data object Initial : EditProfileState()
    data object Loading : EditProfileState()
    data object Success : EditProfileState()
    data object Error : EditProfileState()
}