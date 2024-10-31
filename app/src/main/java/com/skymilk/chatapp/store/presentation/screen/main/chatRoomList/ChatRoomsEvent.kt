package com.skymilk.chatapp.store.presentation.screen.main.chatRoomList

sealed interface ChatRoomsEvent {

    data object LoadChatRooms : ChatRoomsEvent

}