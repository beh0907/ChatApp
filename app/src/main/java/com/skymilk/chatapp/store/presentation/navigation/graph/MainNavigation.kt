package com.skymilk.chatapp.store.presentation.navigation.graph

import android.app.Activity
import android.util.Log
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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
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
import com.skymilk.chatapp.store.presentation.navigation.CustomNavType
import com.skymilk.chatapp.store.presentation.navigation.NavigationViewModel
import com.skymilk.chatapp.store.presentation.navigation.bottom.BottomNavigationItem
import com.skymilk.chatapp.store.presentation.navigation.bottom.MainBottomBar
import com.skymilk.chatapp.store.presentation.navigation.routes.Routes
import com.skymilk.chatapp.store.presentation.screen.main.chatImagePager.ChatImageViewerScreen
import com.skymilk.chatapp.store.presentation.screen.main.chatRoom.ChatRoomScreen
import com.skymilk.chatapp.store.presentation.screen.main.chatRoom.ChatRoomViewModel
import com.skymilk.chatapp.store.presentation.screen.main.chatRoomInvite.ChatRoomInviteScreen
import com.skymilk.chatapp.store.presentation.screen.main.chatRoomInvite.ChatRoomInviteViewModel
import com.skymilk.chatapp.store.presentation.screen.main.chatRoomList.ChatRoomsScreen
import com.skymilk.chatapp.store.presentation.screen.main.chatRoomList.ChatRoomsViewModel
import com.skymilk.chatapp.store.presentation.screen.main.friends.FriendsScreen
import com.skymilk.chatapp.store.presentation.screen.main.friends.FriendsViewModel
import com.skymilk.chatapp.store.presentation.screen.main.profile.ProfileScreen
import com.skymilk.chatapp.store.presentation.screen.main.profile.ProfileViewModel
import com.skymilk.chatapp.store.presentation.screen.main.profileEdit.ProfileEditScreen
import com.skymilk.chatapp.store.presentation.screen.main.profileEdit.ProfileEditViewModel
import com.skymilk.chatapp.store.presentation.screen.main.profileImageViewer.ProfileImageViewerScreen
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
                route = Routes.FriendsScreen(currentUser.id)
            ),
            BottomNavigationItem(
                icon = Icons.AutoMirrored.Outlined.Chat,
                selectedIcon = Icons.AutoMirrored.Rounded.Chat,
                title = "채팅",
                route = Routes.ChatRoomsScreen(currentUser.id)
            ),
            BottomNavigationItem(
                icon = Icons.Outlined.Settings,
                selectedIcon = Icons.Filled.Settings,
                title = "설정",
                route = Routes.SettingScreen
            )
        )
    }

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val selectedItem = remember(key1 = navBackStackEntry) {
        val selectedIndex =
            bottomNavigationItems.indexOfFirst {
                navBackStackEntry?.destination?.route?.startsWith(
                    prefix = it.route::class.qualifiedName.toString()
                ) == true
            }
        if (selectedIndex != -1) selectedIndex else 0
    }

    //하단 바에 표시된 탭 메뉴 화면이 아닐 시 하단 바가 보이지 않게 하기 위해 설정
    val isBottomBarVisible = remember(key1 = navBackStackEntry) {
        bottomNavigationItems.find {
            navBackStackEntry?.destination?.route?.startsWith(
                prefix = it.route::class.qualifiedName.toString()
            ) == true
        } != null
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
            startDestination = bottomNavigationItems[0].route
        ) {
            //친구 목록 화면
            composable<Routes.FriendsScreen> {
                FriendsScreen(
                    viewModel = friendsViewModel,
                    onEvent = friendsViewModel::onEvent,
                    currentUser = currentUser,
                    onNavigateToProfile = { user ->
                        navController.navigate(
                            Routes.ProfileScreen(user = user)
                        ) {
                            // 채팅방 화면으로 이동하기 전에 데이터를 설정합니다.
                            launchSingleTop = true
                        }
                    },
                    onNavigateToUserSearch = {
                        navController.navigate(Routes.UserSearchScreen)
                    }
                )
            }

            //채팅방 목록 화면
            composable<Routes.ChatRoomsScreen> {
                val chatRoomsViewModel: ChatRoomsViewModel = hiltViewModel()

                ChatRoomsScreen(
                    viewModel = chatRoomsViewModel,
                    onEvent = chatRoomsViewModel::onEvent,
                    currentUser = currentUser,
                    onNavigateToChatRoom = { chatRoomId ->
                        navController.navigate(
                            Routes.ChatRoomScreen(
                                chatRoomId = chatRoomId,
                                userId = currentUser.id
                            )
                        ) {
                            // 채팅방 화면으로 이동하기 전에 데이터를 설정합니다.
                            launchSingleTop = true
                        }
                    },
                    onNavigateToChatRoomInvite = {
                        navController.navigate(Routes.ChatRoomInviteScreen())
                    }
                )
            }

            //설정 화면
            composable<Routes.SettingScreen> {
                val settingViewModel: SettingViewModel = hiltViewModel()

                SettingScreen(
                    viewModel = settingViewModel,
                    onEvent = settingViewModel::onEvent,
                    currentUser = currentUser,
                    onNavigateToProfile = { user ->
                        navController.navigate(
                            Routes.ProfileScreen(user = user)
                        )
                    }
                )
            }

            //채팅방 생성 화면
            composable<Routes.ChatRoomInviteScreen> {
                val chatRoomInviteViewModel: ChatRoomInviteViewModel = hiltViewModel()
                val friends by friendsViewModel.friendsState.collectAsStateWithLifecycle()

                //특정 채팅방에서 유저를 추가할 땐 채팅방과 현재 참여자 정보를 가지고 있다
                val args = it.toRoute<Routes.ChatRoomInviteScreen>()

                ChatRoomInviteScreen(
                    viewModel = chatRoomInviteViewModel,
                    onEvent = chatRoomInviteViewModel::onEvent,
                    friendsState = friends,
                    currentUser = currentUser,
                    existingChatRoomId = args.existingChatRoomId,
                    existingParticipants = args.existingParticipants,
                    onNavigateToChatRoom = { chatRoomId ->
                        navController.navigate(
                            Routes.ChatRoomScreen(
                                chatRoomId = chatRoomId,
                                userId = currentUser.id
                            )
                        ) {
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
            composable<Routes.ProfileScreen>(
                typeMap = mapOf(
                    typeOf<User>() to CustomNavType.UserType
                ),
                enterTransition = {
                    when (initialState.destination.route) {
                        Routes.ProfileEditScreen::class.qualifiedName -> {
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
                val argsUser = it.toRoute<Routes.ProfileScreen>().user
                val profileViewModel: ProfileViewModel = hiltViewModel()

                ProfileScreen(
                    viewModel = profileViewModel,
                    onEvent = profileViewModel::onEvent,
                    user = if (currentUser.id == argsUser.id) currentUser else argsUser, //프로필 갱신 시 실시간 반영처리를 위해 currentUser를 넣는다
                    loginUserId = currentUser.id,
                    onNavigateToBack = { navController.popBackStack() },
                    onNavigateToProfileEdit = {
                        navController.navigate(Routes.ProfileEditScreen) {
                            launchSingleTop = true
                        }
                    },
                    onNavigateToChatRoom = { chatRoomId ->
                        navController.navigate(
                            Routes.ChatRoomScreen(
                                chatRoomId = chatRoomId,
                                userId = currentUser.id
                            )
                        ) {
                            popUpTo(bottomNavigationItems[selectedItem].route) {
                                inclusive = false
                            }
                            launchSingleTop = true
                        }
                    },
                    onNavigateToImageViewer = { imageUrl ->
                        navController.navigate(Routes.ProfileImageViewerScreen(imageUrl = imageUrl))
                    },
                    onSignOut = onSignOut
                )
            }

            //프로필 편집 화면
            composable<Routes.ProfileEditScreen> {
                val profileEditViewModel: ProfileEditViewModel = hiltViewModel()

                ProfileEditScreen(
                    viewModel = profileEditViewModel,
                    onEvent = profileEditViewModel::onEvent,
                    user = currentUser,
                    onNavigateToBack = { navController.popBackStack() }
                )
            }

            //프로필 이미지 화면
            composable<Routes.ProfileImageViewerScreen> {
                val args = it.toRoute<Routes.ProfileImageViewerScreen>()

                ProfileImageViewerScreen(
                    imageUrl = args.imageUrl,
                    onNavigateToBack = { navController.popBackStack() }
                )
            }


            //채팅방 화면
            composable<Routes.ChatRoomScreen>(
                deepLinks = listOf(
                    navDeepLink {
                        uriPattern = "chatapp://chatrooms/{chatRoomId}?userId={userId}"
                    }
                )
            ) {
                val args = it.toRoute<Routes.ChatRoomScreen>()
                val chatRoomViewModel: ChatRoomViewModel = hiltViewModel()

                val lifecycleOwner = LocalLifecycleOwner.current
                DisposableEffect(lifecycleOwner) {
                    val observer = LifecycleEventObserver { _, event ->
                        when (event) {
                            Lifecycle.Event.ON_PAUSE -> {
                                // 백그라운드로 갈 때
                                navigationViewmodel.updateCurrentDestination("", null)
                            }

                            Lifecycle.Event.ON_RESUME -> {
                                // 포그라운드로 돌아올 때
                                navigationViewmodel.updateCurrentDestination(
                                    Routes.ChatRoomScreen.javaClass.toString(),
                                    mapOf("chatRoomId" to args.chatRoomId)
                                )
                            }

                            else -> {} // 다른 이벤트 무시
                        }
                    }
                    //옵저버 등록
                    lifecycleOwner.lifecycle.addObserver(observer)

                    //옵저버 제거
                    onDispose {
                        lifecycleOwner.lifecycle.removeObserver(observer)
                    }
                }

                ChatRoomScreen(
                    viewModel = chatRoomViewModel,
                    onEvent = chatRoomViewModel::onEvent,
                    currentUser = currentUser,
                    onNavigateToBack = { navController.popBackStack() },
                    onNavigateToProfile = { user ->
                        navController.navigate(
                            Routes.ProfileScreen(user = user)
                        )
                    },
                    onNavigateToImagePager = { imageUrls, initialPage, senderName, timestamp ->
                        navController.navigate(
                            Routes.ChatImagePagerScreen(
                                imageUrls = imageUrls,
                                initialPage = initialPage,
                                senderName = senderName,
                                timestamp = timestamp
                            )
                        )
                    },
                    onNavigateToInviteFriends = { chatRoomId, participants ->
                        navController.navigate(
                            Routes.ChatRoomInviteScreen(
                                existingChatRoomId = chatRoomId,
                                existingParticipants = participants
                            )
                        )
                    }
                )
            }

            //채팅 이미지 페이저 화면
            composable<Routes.ChatImagePagerScreen> {
                val args = it.toRoute<Routes.ChatImagePagerScreen>()

                ChatImageViewerScreen(
                    imageUrls = args.imageUrls,
                    initialPage = args.initialPage,
                    senderName = args.senderName,
                    timestamp = args.timestamp,
                    onNavigateToBack = { navController.popBackStack() }
                )
            }

            //유저 검색 화면
            composable<Routes.UserSearchScreen> {
                val userSearchViewModel: UserSearchViewModel = hiltViewModel()

                UserSearchScreen(
                    viewModel = userSearchViewModel,
                    onEvent = userSearchViewModel::onEvent,
                    currentUser = currentUser,
                    onNavigateToProfile = { user ->
                        navController.navigate(
                            Routes.ProfileScreen(user = user)
                        )
                    },
                    onNavigateToBack = { navController.popBackStack() }
                )
            }
        }
    }
}

fun navigationToTab(navController: NavController, route: Routes) {
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