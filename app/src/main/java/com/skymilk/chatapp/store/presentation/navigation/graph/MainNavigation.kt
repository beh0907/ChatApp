package com.skymilk.chatapp.store.presentation.navigation.graph

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
import com.skymilk.chatapp.store.presentation.navigation.bottom.BottomNavigationItem
import com.skymilk.chatapp.store.presentation.navigation.CustomNavType
import com.skymilk.chatapp.store.presentation.navigation.NavigationViewModel
import com.skymilk.chatapp.store.presentation.navigation.bottom.MainBottomBar
import com.skymilk.chatapp.store.presentation.navigation.routes.MainScreens
import com.skymilk.chatapp.store.presentation.screen.main.chatRoom.ChatRoomScreen
import com.skymilk.chatapp.store.presentation.screen.main.chatRoom.ChatRoomViewModel
import com.skymilk.chatapp.store.presentation.screen.main.chatRoomInvite.ChatRoomInviteScreen
import com.skymilk.chatapp.store.presentation.screen.main.chatRoomInvite.ChatRoomInviteViewModel
import com.skymilk.chatapp.store.presentation.screen.main.chatRoomList.ChatRoomsScreen
import com.skymilk.chatapp.store.presentation.screen.main.chatRoomList.ChatRoomsViewModel
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
fun MainNavigation(
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
                route = MainScreens.FriendsScreen
            ),
            BottomNavigationItem(
                icon = Icons.AutoMirrored.Outlined.Chat,
                selectedIcon = Icons.AutoMirrored.Rounded.Chat,
                title = "채팅",
                route = MainScreens.ChatsScreen
            ),
            BottomNavigationItem(
                icon = Icons.Outlined.Settings,
                selectedIcon = Icons.Filled.Settings,
                title = "설정",
                route = MainScreens.SettingScreen
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

            MainBottomBar(
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
            startDestination = MainScreens.FriendsScreen
        ) {
            //친구 목록 화면
            composable<MainScreens.FriendsScreen> {

                FriendsScreen(
                    viewModel = friendsViewModel,
                    onEvent = friendsViewModel::onEvent,
                    currentUser = currentUser,
                    onNavigateToProfile = { user ->
                        navController.navigate(
                            MainScreens.ProfileScreen(user = user)
                        ) {
                            // 채팅방 화면으로 이동하기 전에 데이터를 설정합니다.
                            launchSingleTop = true
                        }
                    },
                    onNavigateToUserSearch = {
                        navController.navigate(MainScreens.UserSearchScreen)
                    }
                )
            }

            //채팅방 목록 화면
            composable<MainScreens.ChatsScreen> {
                val chatRoomsViewModel: ChatRoomsViewModel = viewModel(
                    factory = ChatRoomsViewModel.provideFactory(
                        viewModelFactoryProvider.chatListViewModelFactory(),
                        currentUser.id
                    )
                )

                ChatRoomsScreen(
                    viewModel = chatRoomsViewModel,
                    onEvent = chatRoomsViewModel::onEvent,
                    currentUser = currentUser,
                    onNavigateToChatRoom = { chatRoomId ->
                        navController.navigate(MainScreens.ChatRoomScreen(chatRoomId = chatRoomId)) {
                            // 채팅방 화면으로 이동하기 전에 데이터를 설정합니다.
                            launchSingleTop = true
                        }
                    },
                    onNavigateToChatRoomInvite = {
                        navController.navigate(MainScreens.ChatRoomInviteScreen())
                    }
                )
            }

            //설정 화면
            composable<MainScreens.SettingScreen> {
                val settingViewModel: SettingViewModel = hiltViewModel()

                SettingScreen(
                    viewModel = settingViewModel,
                    onEvent = settingViewModel::onEvent,
                    currentUser = currentUser,
                    onNavigateToProfile = { user ->
                        navController.navigate(
                            MainScreens.ProfileScreen(user = user)
                        )
                    }
                )
            }

            //채팅방 생성 화면
            composable<MainScreens.ChatRoomInviteScreen> {
                val chatRoomInviteViewModel: ChatRoomInviteViewModel = hiltViewModel()
                val friends by friendsViewModel.friendsState.collectAsStateWithLifecycle()

                //특정 채팅방에서 유저를 추가할 땐 채팅방과 현재 참여자 정보를 가지고 있다
                val args = it.toRoute<MainScreens.ChatRoomInviteScreen>()

                ChatRoomInviteScreen(
                    viewModel = chatRoomInviteViewModel,
                    onEvent = chatRoomInviteViewModel::onEvent,
                    friendsState = friends,
                    currentUser = currentUser,
                    existingChatRoomId = args.existingChatRoomId,
                    existingParticipants = args.existingParticipants,
                    onNavigateToChatRoom = { chatRoomId ->
                        navController.navigate(MainScreens.ChatRoomScreen(chatRoomId = chatRoomId)) {
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
            composable<MainScreens.ProfileScreen>(
                typeMap = mapOf(
                    typeOf<User>() to CustomNavType.UserType
                ),
                enterTransition = {
                    when (initialState.destination.route) {
                        MainScreens.ProfileEditScreen::class.qualifiedName -> {
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
                val argsUser = it.toRoute<MainScreens.ProfileScreen>().user
                val profileViewModel: ProfileViewModel = hiltViewModel()

                ProfileScreen(
                    viewModel = profileViewModel,
                    onEvent = profileViewModel::onEvent,
                    user = if (currentUser.id == argsUser.id) currentUser else argsUser, //프로필 갱신 시 실시간 반영처리를 위해 currentUser를 넣는다
                    loginUserId = currentUser.id,
                    onNavigateToBack = { navController.popBackStack() },
                    onNavigateToProfileEdit = {
                        navController.navigate(MainScreens.ProfileEditScreen) {
                            launchSingleTop = true
                        }
                    },
                    onNavigateToChatRoom = { chatRoomId ->
                        navController.navigate(MainScreens.ChatRoomScreen(chatRoomId = chatRoomId)) {
                            popUpTo(bottomNavigationItems[selectedItem].route) {
                                inclusive = false
                            }
                            launchSingleTop = true
                        }
                    },
                    onNavigateToImageViewer = { imageUrl ->
                        navController.navigate(MainScreens.ImageViewerScreen(imageUrls = listOf(imageUrl)))
                    },
                    onSignOut = onSignOut
                )
            }

            //프로필 편집 화면
            composable<MainScreens.ProfileEditScreen> {
                val profileEditViewModel: ProfileEditViewModel = hiltViewModel()

                ProfileEditScreen(
                    viewModel = profileEditViewModel,
                    onEvent = profileEditViewModel::onEvent,
                    user = currentUser,
                    onNavigateToBack = { navController.popBackStack() }
                )
            }


            //채팅방 화면
            composable<MainScreens.ChatRoomScreen>(
                deepLinks = listOf(
                    navDeepLink<MainScreens.ChatRoomScreen>(
                        basePath = "chatapp://chatrooms"
                    )
                )
            ) {
                val args = it.toRoute<MainScreens.ChatRoomScreen>()

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
                        MainScreens.ChatRoomScreen.javaClass.toString(),
                        mapOf("chatRoomId" to args.chatRoomId)
                    )

                    //메인화면 종료 시 데이터 초기화
                    onDispose {
                        navigationViewmodel.updateCurrentDestination("", null)
                    }
                }

                ChatRoomScreen(
                    viewModel = chatRoomViewModel,
                    onEvent = chatRoomViewModel::onEvent,
                    currentUser = currentUser,
                    onNavigateToBack = { navController.popBackStack() },
                    onNavigateToProfile = { user ->
                        navController.navigate(
                            MainScreens.ProfileScreen(user = user)
                        )
                    },
                    onNavigateToImageViewer = { imageUrls, startPage  ->
                        navController.navigate(MainScreens.ImageViewerScreen(imageUrls = imageUrls, initialPage = startPage))
                    },
                    onNavigateToInviteFriends = { chatRoomId, participants ->
                        navController.navigate(
                            MainScreens.ChatRoomInviteScreen(
                                existingChatRoomId = chatRoomId,
                                existingParticipants = participants
                            )
                        )
                    }
                )
            }

            //이미지 뷰어 화면
            composable<MainScreens.ImageViewerScreen> {
                val args = it.toRoute<MainScreens.ImageViewerScreen>()

                ImageViewerScreen(
                    imageUrls = args.imageUrls,
                    initialPage = args.initialPage,
                    onNavigateToBack = { navController.popBackStack() }
                )
            }

            //유저 검색 화면
            composable<MainScreens.UserSearchScreen> {
                val userSearchViewModel: UserSearchViewModel = hiltViewModel()

                UserSearchScreen(
                    viewModel = userSearchViewModel,
                    onEvent = userSearchViewModel::onEvent,
                    currentUser = currentUser,
                    onNavigateToProfile = { user ->
                        navController.navigate(
                            MainScreens.ProfileScreen(user = user)
                        )
                    },
                    onNavigateToBack = { navController.popBackStack() }
                )
            }
        }
    }
}

fun navigationToTab(navController: NavController, route: MainScreens) {
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