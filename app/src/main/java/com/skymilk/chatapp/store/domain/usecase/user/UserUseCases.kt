package com.skymilk.chatapp.store.domain.usecase.user

data class UserUseCases(
    val updateProfile: UpdateProfile,
    val getUser: GetUser,
    val getFriends: GetFriends,
    val getIsFriend: GetIsFriend,
    val setFriend: SetFriend,
    val searchUser:SearchUser
)
