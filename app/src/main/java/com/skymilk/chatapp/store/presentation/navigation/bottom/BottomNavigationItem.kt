package com.skymilk.chatapp.store.presentation.navigation.bottom

import androidx.compose.ui.graphics.vector.ImageVector
import com.skymilk.chatapp.store.presentation.navigation.routes.Routes

data class BottomNavigationItem(
    val icon: ImageVector,
    val selectedIcon: ImageVector,
    val title: String = "",
    val badge: Int = 0,
    val route: Routes
)
