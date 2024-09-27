package com.skymilk.chatapp.store.presentation.navigation.routes

import com.skymilk.chatapp.store.domain.model.User
import kotlinx.serialization.Serializable

@Serializable
sealed class MainNavigation {
    //하단 바 메뉴
    @Serializable
    data object FriendsScreen : MainNavigation() // 친구 탭

    @Serializable
    data object ChatsScreen : MainNavigation() // 채팅 목록 탭

    //하단 바가 없는 화면
    @Serializable
    data class ChatRoomScreen(val chatRoomId: String) : MainNavigation() // 채팅방 화면

    @Serializable
    data class ProfileScreen(val user: User) : MainNavigation() // 프로필 화면

    @Serializable
    data object ProfileEditScreen : MainNavigation() // 프로필 편집 화면

    @Serializable
    data class ImageViewerScreen(val imageUrl: String) : MainNavigation() // 이미지 뷰어 화면

    @Serializable
    data object UserSearchScreen : MainNavigation() // 유저 검색 화면

}