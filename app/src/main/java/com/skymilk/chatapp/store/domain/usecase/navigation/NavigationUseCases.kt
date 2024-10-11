package com.skymilk.chatapp.store.domain.usecase.navigation

data class NavigationUseCases(
    val getCurrentDestination: GetCurrentDestination, // 현재 화면 정보 저장
    val saveCurrentDestination: SaveCurrentDestination, // 화면 화면 정보 가져오기
)
