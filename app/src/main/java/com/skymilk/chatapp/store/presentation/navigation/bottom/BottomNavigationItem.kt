package com.skymilk.chatapp.store.presentation.navigation.bottom

import androidx.compose.ui.graphics.vector.ImageVector
import com.skymilk.chatapp.store.presentation.navigation.routes.MainNavigation

data class BottomNavigationItem(
    val icon: ImageVector,
    val selectedIcon: ImageVector,
    val title: String = "",
    val route: MainNavigation
)
