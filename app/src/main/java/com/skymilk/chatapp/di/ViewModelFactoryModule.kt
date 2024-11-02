package com.skymilk.chatapp.di

import com.skymilk.chatapp.store.presentation.screen.main.chatRoom.ChatRoomViewModel
import com.skymilk.chatapp.store.presentation.screen.main.chatRoomList.ChatRoomsViewModel
import com.skymilk.chatapp.store.presentation.screen.main.friends.FriendsViewModel
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

@EntryPoint
@InstallIn(ActivityComponent::class)
interface ViewModelFactoryModule {

    fun chatRoomsViewModelFactory(): ChatRoomsViewModel.Factory

    fun friendsViewModelFactory(): FriendsViewModel.Factory

}