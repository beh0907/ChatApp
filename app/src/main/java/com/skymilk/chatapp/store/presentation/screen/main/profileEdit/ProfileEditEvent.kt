package com.skymilk.chatapp.store.presentation.screen.main.profileEdit

sealed interface ProfileEditEvent {

    data class UpdateUserProfile(
        val userId: String,
        val name: String,
        val statusMessage: String,
        val profileImage: ProfileImage
    ) : ProfileEditEvent

}