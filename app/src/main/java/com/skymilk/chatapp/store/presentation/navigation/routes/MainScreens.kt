package com.skymilk.chatapp.store.presentation.navigation.routes

import com.skymilk.chatapp.store.domain.model.User
import kotlinx.serialization.Serializable

@Serializable
sealed interface MainScreens {
    //하단 바 메뉴
    @Serializable
    data object FriendsScreen : MainScreens // 친구 탭

    @Serializable
    data object ChatsScreen : MainScreens // 채팅 목록 탭

    @Serializable
    data object SettingScreen : MainScreens // 설정 화면

    //하단 바가 없는 화면
    @Serializable
    data class ChatRoomScreen(val chatRoomId: String) : MainScreens // 채팅방 화면

    @Serializable
    data class ChatRoomInviteScreen(
        val existingChatRoomId: String? = null,
        val existingParticipants: List<String> = emptyList()
    ) : MainScreens // 채팅방 대화상대 초대 화면

    @Serializable
    data class ProfileScreen(val user: User) : MainScreens // 프로필 화면

    @Serializable
    data object ProfileEditScreen : MainScreens // 프로필 편집 화면

    @Serializable
    data class ImageViewerScreen(val imageUrls: List<String>, val initialPage: Int = 0) : MainScreens // 이미지 뷰어 화면

    @Serializable
    data object UserSearchScreen : MainScreens // 유저 검색 화면

}