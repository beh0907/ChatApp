package com.skymilk.chatapp.store.domain.usecase.shared

data class SharedUseCases(
    val getCurrentDestination: GetCurrentDestination, // 현재 화면 정보 가져오기
    val setCurrentDestination: SetCurrentDestination, // 현재 화면 정보 저장

    val getSharedFriends: GetSharedFriends, // 친구 목록 가져오기
    val setSharedFriends: SetSharedFriends, // 친구 목록 저장

    val getSelectedChatRoom: GetSelectedChatRoom, // 목록에서 선택한 채팅방 가져오기
    val setSharedChatRooms: SetSharedChatRooms, // 채팅방 목록 저장하기
)
