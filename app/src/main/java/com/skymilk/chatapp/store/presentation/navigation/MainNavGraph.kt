package com.skymilk.chatapp.store.presentation.navigation

import android.app.Activity
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.skymilk.chatapp.di.ViewModelFactoryModule
import com.skymilk.chatapp.store.domain.model.ChatRoomWithUsers
import com.skymilk.chatapp.store.domain.model.User
import com.skymilk.chatapp.store.presentation.screen.main.chatList.ChatListScreen
import com.skymilk.chatapp.store.presentation.screen.main.chatList.ChatListViewModel
import com.skymilk.chatapp.store.presentation.screen.main.chatRoom.ChatRoomScreen
import com.skymilk.chatapp.store.presentation.screen.main.chatRoom.ChatRoomViewModel
import com.skymilk.chatapp.store.presentation.screen.main.friends.FriendsScreen
import com.skymilk.chatapp.store.presentation.screen.main.friends.FriendsViewModel
import com.skymilk.chatapp.store.presentation.screen.main.profile.ProfileScreen
import com.skymilk.chatapp.store.presentation.screen.main.profile.ProfileViewModel
import dagger.hilt.android.EntryPointAccessors

@Composable
fun MainNavGraph(
    currentUser: User,
    onSignOut: () -> Unit
) {
    //하단 탭 메뉴
    val bottomNavigationItems = remember {
        listOf(
            BottomNavigationItem(
                icon = Icons.Outlined.People,
                selectedIcon = Icons.Filled.People,
                title = "친구"
            ),
            BottomNavigationItem(
                icon = Icons.AutoMirrored.Outlined.Chat,
                selectedIcon = Icons.AutoMirrored.Default.Chat,
                title = "채팅"
            ),
            BottomNavigationItem(
                icon = Icons.Outlined.Person,
                selectedIcon = Icons.Filled.Person,
                title = "프로필"
            )
        )
    }

    val navController = rememberNavController()
    val backStackState by navController.currentBackStackEntryAsState()
    var selectedItem by rememberSaveable {
        mutableIntStateOf(0)
    }

    //현재 선택된 탭 위치
    selectedItem = remember(key1 = backStackState) {
        when (backStackState?.destination?.route) {
            Routes.FriendsScreen.route -> 0
            Routes.ChatListScreen.route -> 1
            Routes.ProfileScreen.route -> 2
            else -> 0
        }
    }

    //하단 바에 표시된 탭 메뉴 화면이 아닐 시 하단 바가 보이지 않게 하기 위해 설정
    val isBottomBarVisible = remember(key1 = backStackState) {
        backStackState?.destination?.route == Routes.FriendsScreen.route ||
                backStackState?.destination?.route == Routes.ChatListScreen.route ||
                backStackState?.destination?.route == Routes.ProfileScreen.route
    }

    val context = LocalContext.current
    val viewModelFactoryProvider = EntryPointAccessors.fromActivity(
        context as Activity,
        ViewModelFactoryModule::class.java
    )

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
                            Routes.FriendsScreen.route
                        )

                        1 -> navigationToTab(
                            navController,
                            Routes.ChatListScreen.route
                        )

                        2 -> navigationToTab(
                            navController,
                            Routes.ProfileScreen.route
                        )
                    }
                }
            )
        }
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = Routes.FriendsScreen.route,
        ) {
            //친구 목록 화면
            composable(Routes.FriendsScreen.route) {
                val friendsViewModel: FriendsViewModel = viewModel(
                    factory = FriendsViewModel.provideFactory(
                        viewModelFactoryProvider.friendsViewModelFactory(),
                        currentUser.id
                    )
                )

                FriendsScreen(
                    modifier = Modifier.padding(innerPadding),
                    viewModel = friendsViewModel
                )
            }

            //채팅방 목록 화면
            composable(Routes.ChatListScreen.route) {
                val chatListViewModel: ChatListViewModel = viewModel(
                    factory = ChatListViewModel.provideFactory(
                        viewModelFactoryProvider.chatListViewModelFactory(),
                        currentUser.id
                    )
                )

                ChatListScreen(
                    modifier = Modifier.padding(innerPadding),
                    viewModel = chatListViewModel,
                    currentUser = currentUser,
                    onChatItemClick = { chatRoom ->
                        navigationToChatRoom(navController, chatRoom)
                    }
                )
            }

            //프로필 화면
            composable(Routes.ProfileScreen.route) {
                val profileViewModel: ProfileViewModel = viewModel(
                    factory = ProfileViewModel.provideFactory(
                        viewModelFactoryProvider.profileViewModelFactory(),
                        currentUser.id
                    )
                )

                ProfileScreen(
                    modifier = Modifier.padding(innerPadding),
                    viewModel = profileViewModel, currentUser = currentUser, onSignOut = onSignOut
                )
            }

            //채팅방 화면
            composable(route = Routes.ChatRoomScreen.route) {
                val chatRoom by remember {
                    mutableStateOf(
                        navController.currentBackStackEntry?.savedStateHandle?.get<ChatRoomWithUsers>(
                            "chatRoom"
                        )
                    )
                }

                chatRoom?.let {
                    val chatRoomViewModel: ChatRoomViewModel = viewModel(
                        factory = ChatRoomViewModel.provideFactory(
                            viewModelFactoryProvider.chatRoomViewModelFactory(),
                            it
                        )
                    )

                    ChatRoomScreen(
                        modifier = Modifier.padding(innerPadding),
                        viewModel = chatRoomViewModel,
                        chatRoom = it,
                        currentUser = currentUser,
                        onNavigateToBack = {
                            navController.popBackStack()
                        }
                    )
                }
            }
        }
    }
}

fun navigationToTab(navController: NavController, route: String) {
    navController.navigate(route) {
        navController.graph.startDestinationRoute?.let { screen ->
            popUpTo(screen) {
                saveState = true
            }
            restoreState = true
            launchSingleTop = true
        }
    }
}

fun navigationToChatRoom(navController: NavController, chatRoom: ChatRoomWithUsers) {
    navController.navigate(Routes.ChatRoomScreen.route) {
        // 채팅방 화면으로 이동하기 전에 데이터를 설정합니다.
        launchSingleTop = true
    }
    // 이동 후 백 스택 엔트리에 데이터를 설정합니다.
    navController.currentBackStackEntry?.savedStateHandle?.set("chatRoom", chatRoom)
}