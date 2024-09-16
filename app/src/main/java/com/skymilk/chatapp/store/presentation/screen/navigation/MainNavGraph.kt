package com.skymilk.chatapp.store.presentation.screen.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseUser
import com.skymilk.chatapp.store.domain.model.ChatRoom
import com.skymilk.chatapp.store.presentation.screen.main.chat.ChatListScreen
import com.skymilk.chatapp.store.presentation.screen.main.chat.ChatRoomScreen
import com.skymilk.chatapp.store.presentation.screen.main.friends.FriendsScreen
import com.skymilk.chatapp.store.presentation.screen.main.profile.ProfileScreen

@Composable
fun MainNavGraph(
    currentUser: FirebaseUser,
    onSignOut: () -> Unit,
    onNavigateToChatRoom: (String) -> Unit
) {
    //하단 탭 메뉴
    val bottomNavigationItems = remember {
        listOf(
            BottomNavigationItem(icon = Icons.Filled.People, title = "친구"),
            BottomNavigationItem(icon = Icons.AutoMirrored.Default.Chat, title = "채팅"),
            BottomNavigationItem(icon = Icons.Filled.Person, title = "프로필")
        )
    }

    val navController = rememberNavController()
    val backStackState = navController.currentBackStackEntryAsState().value
    var selectedItem by rememberSaveable {
        mutableIntStateOf(0)
    }

    //현재 선택된 탭 위치
    selectedItem = remember(key1 = backStackState) {
        when (backStackState?.destination?.route) {
            Screens.FriendsScreen.route -> 0
            Screens.ChatListScreen.route -> 1
            Screens.ProfileScreen.route -> 2
            else -> 0
        }
    }

    //하단 바에 표시된 탭 메뉴 화면이 아닐 시 하단 바가 보이지 않게 하기 위해 설정
    val isBottomBarVisible = remember(key1 = backStackState) {
        backStackState?.destination?.route == Screens.FriendsScreen.route ||
                backStackState?.destination?.route == Screens.ChatListScreen.route ||
                backStackState?.destination?.route == Screens.ProfileScreen.route
    }

    Scaffold(
        bottomBar = {
            //isBottomBarVisible 상태 정보 체크 처리
            if (!isBottomBarVisible) return@Scaffold

            MainBottomNavigation(
                items = bottomNavigationItems,
                selected = selectedItem,
                onItemClick = { index ->
                    when (index) {
                        0 -> navigationToTab(
                            navController,
                            Screens.FriendsScreen.route
                        )

                        1 -> navigationToTab(
                            navController,
                            Screens.ChatListScreen.route
                        )

                        2 -> navigationToTab(
                            navController,
                            Screens.ProfileScreen.route
                        )
                    }
                }
            )
        }
    ) { _ ->
        NavHost(
            navController = navController,
            startDestination = Screens.FriendsScreen.route,
        ) {
            //친구 목록 화면
            composable(Screens.FriendsScreen.route) {
                FriendsScreen()
            }

            //채팅방 목록 화면
            composable(Screens.ChatListScreen.route) {
                ChatListScreen(onChatItemClick = { chatId ->
                    onNavigateToChatRoom(chatId)
                })
            }

            //프로필 화면
            composable(Screens.ProfileScreen.route) {
                ProfileScreen(currentUser = currentUser, onSignOut = onSignOut)
            }

            //채팅방 화면
            composable(Screens.ChatRoomScreen.route) {
                val chatRoom = navController.previousBackStackEntry?.savedStateHandle?.get<ChatRoom>("chatRoom")
                ChatRoomScreen()
            }
        }
    }
}

fun navigationToTab(navController: NavController, route: String) {
    navController.navigate(route) {
        navController.graph.startDestinationRoute?.let { homeScreen ->
            popUpTo(homeScreen) {
                saveState = true
            }
            restoreState = true
            launchSingleTop = true
        }
    }
}

//fun navigationToChatRoom(navController: NavController, chatRoom: ChatRoom) {
//    navController.currentBackStackEntry?.savedStateHandle?.set("chatRoom", chatRoom)
//
//    navController.navigate(
//        route = Screens.ChatRoomScreen.route
//    )
//}