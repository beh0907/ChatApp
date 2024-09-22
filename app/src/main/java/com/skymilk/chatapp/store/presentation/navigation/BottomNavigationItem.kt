package com.skymilk.chatapp.store.presentation.navigation

import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavigationItem(
    val icon: ImageVector,
    val selectedIcon: ImageVector,
    val title: String = "",
    val route: String = ""
)
