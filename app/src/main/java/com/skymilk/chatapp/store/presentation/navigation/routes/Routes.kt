package com.skymilk.chatapp.store.presentation.navigation.routes

import com.skymilk.chatapp.store.domain.model.User
import kotlinx.serialization.Serializable

@Serializable
sealed interface Routes {

    //시작 화면
    //////////////////////////////////////////////////////
    @Serializable
    data object StartGraph : Routes

    @Serializable
    data object SplashScreen : Routes// 로딩 화면
    //////////////////////////////////////////////////////

    //로그인/회원가입 화면
    //////////////////////////////////////////////////////
    @Serializable
    data object AuthGraph : Routes

    @Serializable
    data object SignInScreen : Routes // 로그인 화면

    @Serializable
    data object SignUpScreen : Routes // 회원가입 화면
    //////////////////////////////////////////////////////

    //메인 화면
    //////////////////////////////////////////////////////
    @Serializable
    data object MainGraph : Routes

    //하단 바 메뉴
    @Serializable
    data object FriendsScreen : Routes // 친구 탭

    @Serializable
    data object ChatsScreen : Routes // 채팅 목록 탭

    @Serializable
    data object SettingScreen : Routes // 설정 화면

    //하단 바가 없는 화면
    @Serializable
    data class ChatRoomScreen(val chatRoomId: String) : Routes // 채팅방 화면

    @Serializable
    data class ChatRoomInviteScreen(
        val existingChatRoomId: String? = null,
        val existingParticipants: List<String> = emptyList()
    ) : Routes // 채팅방 대화상대 초대 화면

    @Serializable
    data class ChatImagePagerScreen(val imageUrls: List<String>, val initialPage: Int = 0) :
        Routes // 채팅 이미지 페이저 화면

    @Serializable
    data class ProfileScreen(val user: User) : Routes // 프로필 화면

    @Serializable
    data object ProfileEditScreen : Routes // 프로필 편집 화면

    @Serializable
    data class ProfileImageViewerScreen(val imageUrl: String) : Routes // 프로필 이미지 뷰어 화면

    @Serializable
    data object UserSearchScreen : Routes // 유저 검색 화면

    //////////////////////////////////////////////////////
}