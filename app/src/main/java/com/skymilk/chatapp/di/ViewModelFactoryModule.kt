package com.skymilk.chatapp.di

import com.skymilk.chatapp.store.presentation.screen.main.chatList.ChatListViewModel
import com.skymilk.chatapp.store.presentation.screen.main.chatRoom.ChatRoomViewModel
import com.skymilk.chatapp.store.presentation.screen.main.friends.FriendsViewModel
import com.skymilk.chatapp.store.presentation.screen.main.profile.ProfileViewModel
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

@EntryPoint
@InstallIn(ActivityComponent::class)
interface ViewModelFactoryModule {

    fun chatListViewModelFactory(): ChatListViewModel.Factory

    fun friendsViewModelFactory(): FriendsViewModel.Factory

    fun profileViewModelFactory(): ProfileViewModel.Factory

    fun chatRoomViewModelFactory(): ChatRoomViewModel.Factory

}