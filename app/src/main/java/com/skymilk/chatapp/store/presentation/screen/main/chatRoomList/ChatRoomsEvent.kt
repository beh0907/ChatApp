package com.skymilk.chatapp.store.presentation.screen.main.chatRoomList

import androidx.compose.ui.graphics.ImageBitmap

sealed interface ChatRoomsEvent {

    data object LoadChatRooms : ChatRoomsEvent

    data object LoadChatRoomSetting : ChatRoomsEvent

}