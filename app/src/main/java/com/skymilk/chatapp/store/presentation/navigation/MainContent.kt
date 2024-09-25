package com.skymilk.chatapp.store.presentation.navigation

import android.app.Activity
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.outlined.People
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.util.fastMap
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.skymilk.chatapp.di.ViewModelFactoryModule
import com.skymilk.chatapp.store.domain.model.User
import com.skymilk.chatapp.store.presentation.navigation.routes.MainNavigation
import com.skymilk.chatapp.store.presentation.navigation.routes.Navigations
import com.skymilk.chatapp.store.presentation.screen.main.chatRoom.ChatRoomScreen
import com.skymilk.chatapp.store.presentation.screen.main.chatRoom.ChatRoomViewModel
import com.skymilk.chatapp.store.presentation.screen.main.chatRoomList.ChatListScreen
import com.skymilk.chatapp.store.presentation.screen.main.chatRoomList.ChatRoomListViewModel
import com.skymilk.chatapp.store.presentation.screen.main.friends.FriendsScreen
import com.skymilk.chatapp.store.presentation.screen.main.friends.FriendsViewModel
import com.skymilk.chatapp.store.presentation.screen.main.profile.ProfileScreen
import com.skymilk.chatapp.store.presentation.screen.main.profile.ProfileViewModel
import com.skymilk.chatapp.store.presentation.screen.main.profileEdit.ProfileEditScreen
import com.skymilk.chatapp.store.presentation.screen.main.profileEdit.ProfileEditViewModel
import dagger.hilt.android.EntryPointAccessors

@Composable
fun MainContent(
    currentUser: User,
    onSignOut: () -> Unit,
    parentNavController: NavHostController
) {
    //하단 탭 메뉴
    val bottomNavigationItems = remember {
        listOf(
            BottomNavigationItem(
                icon = Icons.Outlined.People,
                selectedIcon = Icons.Filled.People,
                title = "친구",
                route = MainNavigation.FriendsScreen.route
            ),
            BottomNavigationItem(
                icon = Icons.AutoMirrored.Outlined.Chat,
                selectedIcon = Icons.AutoMirrored.Default.Chat,
                title = "채팅",
                route = MainNavigation.ChatsScreen.route
            ),
//            BottomNavigationItem(
//                icon = Icons.Outlined.Person,
//                selectedIcon = Icons.Filled.Person,
//                title = "프로필",
//                route = MainNavigation.ProfileScreen.route
//            )
        )
    }

    val navController = rememberNavController()
    val backStackState by navController.currentBackStackEntryAsState()
    var selectedItem by rememberSaveable { mutableIntStateOf(0) }

    //현재 선택된 탭 위치
    selectedItem = remember(key1 = backStackState) {
        val selectedIndex =
            bottomNavigationItems.indexOfFirst { it.route == backStackState?.destination?.route }
        if (selectedIndex != -1) selectedIndex else 0
    }

    //하단 바에 표시된 탭 메뉴 화면이 아닐 시 하단 바가 보이지 않게 하기 위해 설정
    val isBottomBarVisible = remember(key1 = backStackState) {
        backStackState?.destination?.route in bottomNavigationItems.fastMap { it.route }
    }

    //뷰모델 프로바이더
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
                    navigationToTab(
                        navController,
                        bottomNavigationItems[index].route
                    )
                }
            )
        }
    ) { innerPadding ->

        NavHost(
            modifier = Modifier.padding(innerPadding),
            navController = navController,
            startDestination = MainNavigation.FriendsScreen.route
        ) {
            //친구 목록 화면
            composable(MainNavigation.FriendsScreen.route) {
                val friendsViewModel: FriendsViewModel = viewModel(
                    factory = FriendsViewModel.provideFactory(
                        viewModelFactoryProvider.friendsViewModelFactory(),
                        currentUser.id
                    )
                )

                FriendsScreen(
                    modifier = Modifier,
                    currentUser = currentUser,
                    viewModel = friendsViewModel,
                    onNavigateToProfile = {
                        navController.navigate(MainNavigation.ProfileScreen.route) {
                            // 채팅방 화면으로 이동하기 전에 데이터를 설정합니다.
                            launchSingleTop = true
                        }
                    }
                )
            }

            //채팅방 목록 화면
            composable(MainNavigation.ChatsScreen.route) {
                val chatRoomListViewModel: ChatRoomListViewModel = viewModel(
                    factory = ChatRoomListViewModel.provideFactory(
                        viewModelFactoryProvider.chatListViewModelFactory(),
                        currentUser.id
                    )
                )

                ChatListScreen(
                    modifier = Modifier,
                    viewModel = chatRoomListViewModel,
                    currentUser = currentUser,
                    onChatItemClick = { chatRoomId ->
                        navController.navigate(MainNavigation.ChatRoomScreen.route + "/$chatRoomId") {
                            // 채팅방 화면으로 이동하기 전에 데이터를 설정합니다.
                            launchSingleTop = true
                        }
                    }
                )
            }

            //프로필 화면
            composable(
                route = MainNavigation.ProfileScreen.route,
            ) {
                val profileViewModel: ProfileViewModel = hiltViewModel()

                ProfileScreen(
                    modifier = Modifier,
                    viewModel = profileViewModel,
                    user = currentUser,
                    onNavigateToBack = {
                        navController.popBackStack()
                    },
                    onNavigateToProfileEdit = {
                        navController.navigate(MainNavigation.ProfileEditScreen.route) {
                            launchSingleTop = true
                        }
                    },
                    onNavigateToChatRoom = { chatRoomId ->
                        navController.navigate(MainNavigation.ChatRoomScreen.route + "/$chatRoomId") {
                            popUpTo(MainNavigation.ProfileScreen.route) { inclusive = true }

                            // 채팅방 화면으로 이동하기 전에 데이터를 설정합니다.
                            launchSingleTop = true
                        }
                    },
                    onSignOut = onSignOut
                )
            }

            //프로필 편집 화면
            composable(MainNavigation.ProfileEditScreen.route) {
                val profileEditViewModel: ProfileEditViewModel = hiltViewModel()

                ProfileEditScreen(
                    modifier = Modifier,
                    viewModel = profileEditViewModel,
                    user = currentUser,
                    onNavigateToBack = {
                        navController.popBackStack()
                    }
                )
            }


            //채팅방 화면
            composable(
                route = MainNavigation.ChatRoomScreen.route + "/{chatRoomId}",
                arguments = listOf(navArgument("chatRoomId") { type = NavType.StringType }),
                deepLinks = listOf(
                    navDeepLink {
                        uriPattern = "chatapp://chatrooms/{chatRoomId}"
                    }
                )
            ) { backStackEntry ->

                val chatRoomId = backStackEntry.arguments?.getString("chatRoomId")
                chatRoomId?.let {
                    val chatRoomViewModel: ChatRoomViewModel = viewModel(
                        factory = ChatRoomViewModel.provideFactory(
                            viewModelFactoryProvider.chatRoomViewModelFactory(),
                            chatRoomId
                        )
                    )

                    ChatRoomScreen(
                        modifier = Modifier,
                        viewModel = chatRoomViewModel,
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