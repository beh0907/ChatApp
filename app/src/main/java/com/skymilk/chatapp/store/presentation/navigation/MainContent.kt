package com.skymilk.chatapp.store.presentation.navigation

import android.app.Activity
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.automirrored.rounded.Chat
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import androidx.navigation.toRoute
import com.skymilk.chatapp.di.ViewModelFactoryModule
import com.skymilk.chatapp.store.domain.model.User
import com.skymilk.chatapp.store.presentation.navigation.routes.MainNavigation
import com.skymilk.chatapp.store.presentation.screen.main.chatRoom.ChatRoomScreen
import com.skymilk.chatapp.store.presentation.screen.main.chatRoom.ChatRoomViewModel
import com.skymilk.chatapp.store.presentation.screen.main.chatRoomInvite.ChatRoomInviteScreen
import com.skymilk.chatapp.store.presentation.screen.main.chatRoomInvite.ChatRoomInviteViewModel
import com.skymilk.chatapp.store.presentation.screen.main.chatRoomList.ChatListScreen
import com.skymilk.chatapp.store.presentation.screen.main.chatRoomList.ChatRoomListViewModel
import com.skymilk.chatapp.store.presentation.screen.main.friends.FriendsScreen
import com.skymilk.chatapp.store.presentation.screen.main.friends.FriendsViewModel
import com.skymilk.chatapp.store.presentation.screen.main.imageViewer.ImageViewerScreen
import com.skymilk.chatapp.store.presentation.screen.main.profile.ProfileScreen
import com.skymilk.chatapp.store.presentation.screen.main.profile.ProfileViewModel
import com.skymilk.chatapp.store.presentation.screen.main.profileEdit.ProfileEditScreen
import com.skymilk.chatapp.store.presentation.screen.main.profileEdit.ProfileEditViewModel
import com.skymilk.chatapp.store.presentation.screen.main.setting.SettingScreen
import com.skymilk.chatapp.store.presentation.screen.main.setting.SettingViewModel
import com.skymilk.chatapp.store.presentation.screen.main.userSearch.UserSearchScreen
import com.skymilk.chatapp.store.presentation.screen.main.userSearch.UserSearchViewModel
import dagger.hilt.android.EntryPointAccessors
import kotlin.reflect.typeOf

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
                route = MainNavigation.FriendsScreen
            ),
            BottomNavigationItem(
                icon = Icons.AutoMirrored.Outlined.Chat,
                selectedIcon = Icons.AutoMirrored.Rounded.Chat,
                title = "채팅",
                route = MainNavigation.ChatsScreen
            ),
            BottomNavigationItem(
                icon = Icons.Outlined.Settings,
                selectedIcon = Icons.Filled.Settings,
                title = "설정",
                route = MainNavigation.SettingScreen
            )
        )
    }

    val navController = rememberNavController()
    val backStackState by navController.currentBackStackEntryAsState()
    val selectedItem = remember(key1 = backStackState) {
        val selectedIndex =
            bottomNavigationItems.indexOfFirst { it.route::class.qualifiedName == backStackState?.destination?.route }
        if (selectedIndex != -1) selectedIndex else 0
    }

    //하단 바에 표시된 탭 메뉴 화면이 아닐 시 하단 바가 보이지 않게 하기 위해 설정
    val isBottomBarVisible = remember(key1 = backStackState) {
        bottomNavigationItems.find { it.route::class.qualifiedName == backStackState?.destination?.route } != null
    }

    //뷰모델 프로바이더
    val context = LocalContext.current
    val viewModelFactoryProvider = EntryPointAccessors.fromActivity(
        context as Activity,
        ViewModelFactoryModule::class.java
    )

    //친구 viewmodel 공유
    val friendsViewModel: FriendsViewModel = viewModel(
        factory = FriendsViewModel.provideFactory(
            viewModelFactoryProvider.friendsViewModelFactory(),
            currentUser.id
        )
    )

    //네비게이션 화면 상태 저장 viewmodel
    val navigationViewmodel: NavigationViewModel = hiltViewModel()

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
            startDestination = MainNavigation.FriendsScreen
        ) {
            //친구 목록 화면
            composable<MainNavigation.FriendsScreen> {

                FriendsScreen(
                    currentUser = currentUser,
                    viewModel = friendsViewModel,
                    onNavigateToProfile = { user ->
                        navController.navigate(
                            MainNavigation.ProfileScreen(user = user)
                        ) {
                            // 채팅방 화면으로 이동하기 전에 데이터를 설정합니다.
                            launchSingleTop = true
                        }
                    },
                    onNavigateToUserSearch = {
                        navController.navigate(MainNavigation.UserSearchScreen)
                    }
                )
            }

            //채팅방 목록 화면
            composable<MainNavigation.ChatsScreen> {
                val chatRoomListViewModel: ChatRoomListViewModel = viewModel(
                    factory = ChatRoomListViewModel.provideFactory(
                        viewModelFactoryProvider.chatListViewModelFactory(),
                        currentUser.id
                    )
                )

                ChatListScreen(
                    viewModel = chatRoomListViewModel,
                    currentUser = currentUser,
                    onNavigateToChatRoom = { chatRoomId ->
                        navController.navigate(MainNavigation.ChatRoomScreen(chatRoomId = chatRoomId)) {
                            // 채팅방 화면으로 이동하기 전에 데이터를 설정합니다.
                            launchSingleTop = true
                        }
                    },
                    onNavigateToChatRoomInvite = {
                        navController.navigate(MainNavigation.ChatRoomInviteScreen())
                    }
                )
            }

            //설정 화면
            composable<MainNavigation.SettingScreen> {
                val settingViewModel: SettingViewModel = hiltViewModel()

                SettingScreen(
                    currentUser = currentUser,
                    viewModel = settingViewModel,
                    onNavigateToProfile = { user ->
                        navController.navigate(
                            MainNavigation.ProfileScreen(user = user)
                        )
                    }
                )
            }

            //채팅방 생성 화면
            composable<MainNavigation.ChatRoomInviteScreen> {
                val chatRoomInviteViewModel: ChatRoomInviteViewModel = hiltViewModel()
                val friends by friendsViewModel.friendsState.collectAsStateWithLifecycle()

                //특정 채팅방에서 유저를 추가할 땐 채팅방과 현재 참여자 정보를 가지고 있다
                val args = it.toRoute<MainNavigation.ChatRoomInviteScreen>()

                ChatRoomInviteScreen(
                    viewModel = chatRoomInviteViewModel,
                    friendsState = friends,
                    currentUser = currentUser,
                    existingChatRoomId = args.existingChatRoomId,
                    existingParticipants = args.existingParticipants,
                    onNavigateToChatRoom = { chatRoomId ->
                        navController.navigate(MainNavigation.ChatRoomScreen(chatRoomId = chatRoomId)) {
                            popUpTo(bottomNavigationItems[selectedItem].route) {
                                inclusive = false
                            }
                            launchSingleTop = true
                        }
                    },
                    onNavigateToBack = { navController.popBackStack() }
                )
            }

            //프로필 화면
            composable<MainNavigation.ProfileScreen>(
                typeMap = mapOf(
                    typeOf<User>() to CustomNavType.UserType
                ),
                enterTransition = {
                    when (initialState.destination.route) {
                        MainNavigation.ProfileEditScreen::class.qualifiedName -> {
                            EnterTransition.None
                        }

                        else -> {
                            slideInVertically(
                                initialOffsetY = { it },
                                animationSpec = tween(300)
                            )
                        }
                    }
                },
                popExitTransition = {
                    // 화면이 닫힐 때 위에서 아래로 슬라이드
                    slideOutVertically(
                        targetOffsetY = { it },
                        animationSpec = tween(300)
                    )
                }
            ) {
                val argsUser = it.toRoute<MainNavigation.ProfileScreen>().user
                val profileViewModel: ProfileViewModel = hiltViewModel()

                ProfileScreen(
                    viewModel = profileViewModel,
                    user = if (currentUser.id == argsUser.id) currentUser else argsUser, //프로필 갱신 시 실시간 반영처리를 위해 currentUser를 넣는다
                    loginUserId = currentUser.id,
                    onNavigateToBack = { navController.popBackStack() },
                    onNavigateToProfileEdit = {
                        navController.navigate(MainNavigation.ProfileEditScreen) {
                            launchSingleTop = true
                        }
                    },
                    onNavigateToChatRoom = { chatRoomId ->
                        navController.navigate(MainNavigation.ChatRoomScreen(chatRoomId = chatRoomId)) {
                            popUpTo(bottomNavigationItems[selectedItem].route) {
                                inclusive = false
                            }
                            launchSingleTop = true
                        }
                    },
                    onNavigateToImageViewer = { imageUrl ->
                        navController.navigate(MainNavigation.ImageViewerScreen(imageUrl = imageUrl))
                    },
                    onSignOut = onSignOut
                )
            }

            //프로필 편집 화면
            composable<MainNavigation.ProfileEditScreen> {
                val profileEditViewModel: ProfileEditViewModel = hiltViewModel()

                ProfileEditScreen(
                    viewModel = profileEditViewModel,
                    user = currentUser,
                    onNavigateToBack = { navController.popBackStack() }
                )
            }


            //채팅방 화면
            composable<MainNavigation.ChatRoomScreen>(
                deepLinks = listOf(
                    navDeepLink<MainNavigation.ChatRoomScreen>(
                        basePath = "chatapp://chatrooms"
                    )
                )
            ) {
                val args = it.toRoute<MainNavigation.ChatRoomScreen>()

                val chatRoomViewModel: ChatRoomViewModel = viewModel(
                    factory = ChatRoomViewModel.provideFactory(
                        viewModelFactoryProvider.chatRoomViewModelFactory(),
                        args.chatRoomId
                    )
                )

                //채팅방에 진입할 때 채팅방 화면 정보와 파라미터 전달
                DisposableEffect(Unit) {
                    args.javaClass.toString()
                    // 현재 화면 정보 저장
                    navigationViewmodel.updateCurrentDestination(
                        MainNavigation.ChatRoomScreen.javaClass.toString(),
                        mapOf("chatRoomId" to args.chatRoomId)
                    )

                    //메인화면 종료 시 데이터 초기화
                    onDispose {
                        navigationViewmodel.updateCurrentDestination("", null)
                    }
                }

                ChatRoomScreen(
                    viewModel = chatRoomViewModel,
                    currentUser = currentUser,
                    onNavigateToBack = { navController.popBackStack() },
                    onNavigateToProfile = { user ->
                        navController.navigate(
                            MainNavigation.ProfileScreen(user = user)
                        )
                    },
                    onNavigateToImageViewer = { imageUrl ->
                        navController.navigate(MainNavigation.ImageViewerScreen(imageUrl = imageUrl))
                    },
                    onNavigateToInviteFriends = { chatRoomId, participants ->
                        navController.navigate(
                            MainNavigation.ChatRoomInviteScreen(
                                existingChatRoomId = chatRoomId,
                                existingParticipants = participants
                            )
                        )
                    }
                )
            }

            //이미지 뷰어 화면
            composable<MainNavigation.ImageViewerScreen> {
                val args = it.toRoute<MainNavigation.ImageViewerScreen>()

                ImageViewerScreen(
                    imageUrl = args.imageUrl,
                    onNavigateToBack = { navController.popBackStack() }
                )
            }

            //유저 검색 화면
            composable<MainNavigation.UserSearchScreen> {
                val userSearchViewModel: UserSearchViewModel = hiltViewModel()

                UserSearchScreen(
                    currentUser = currentUser,
                    viewModel = userSearchViewModel,
                    onNavigateToProfile = { user ->
                        navController.navigate(
                            MainNavigation.ProfileScreen(user = user)
                        )
                    },
                    onNavigateToBack = { navController.popBackStack() }
                )
            }
        }
    }
}

fun navigationToTab(navController: NavController, route: MainNavigation) {
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