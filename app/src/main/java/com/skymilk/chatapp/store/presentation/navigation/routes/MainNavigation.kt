package com.skymilk.chatapp.store.presentation.navigation.routes

sealed class MainNavigation(val route: String) {
    //하단 바 메뉴
    data object FriendsScreen : MainNavigation("FriendsScreen") // 친구 탭
    data object ChatsScreen : MainNavigation("ChatsScreen") // 채팅 목록 탭
    data object ProfileScreen : MainNavigation("ProfileScreen") // 프로필 탭

    //하단 바가 없는 화면
    data object ChatRoomScreen : MainNavigation("ChatRoomScreen") // 채팅방 화면
    data object ProfileEditScreen : MainNavigation("ProfileEditScreen") // 프로필 편집 화면

}