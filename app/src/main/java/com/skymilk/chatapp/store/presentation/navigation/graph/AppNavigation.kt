package com.skymilk.chatapp.store.presentation.navigation.graph

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.skymilk.chatapp.store.presentation.navigation.routes.Routes
import com.skymilk.chatapp.store.presentation.screen.auth.AuthEvent
import com.skymilk.chatapp.store.presentation.screen.auth.AuthState
import com.skymilk.chatapp.store.presentation.screen.auth.AuthViewModel
import com.skymilk.chatapp.store.presentation.screen.auth.signIn.SignInScreen
import com.skymilk.chatapp.store.presentation.screen.auth.signUp.SignUpScreen
import com.skymilk.chatapp.store.presentation.screen.splash.SplashScreen

@Composable
fun AppNavigation(
    isDeepLink: Boolean,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    //계정 처리 뷰모델 및 계정 상태
    val authState by authViewModel.authState.collectAsStateWithLifecycle()

    //화면 네비게이션 설정
    val navController = rememberNavController()

    //앱 시작 시 화면
    val startGraph = if (isDeepLink) Routes.MainGraph else Routes.StartGraph // 딥링크 정보가 있다면 로딩화면 스킵

    NavHost(
        navController = navController,
        startDestination = startGraph
    ) {
        //시작 네비게이션
        navigation<Routes.StartGraph>(
            startDestination = Routes.SplashScreen
        ) {
            composable<Routes.SplashScreen> {
                SplashScreen(
                    onAnimationFinished = {
                        //로딩 후 이동 화면
                        val destination =
                            if (authState is AuthState.Authenticated) Routes.MainGraph else Routes.AuthGraph

                        navController.navigate(destination) {
                            popUpTo(Routes.StartGraph) { inclusive = true }
                        }
                    }
                )
            }
        }

        //인증 네비게이션
        navigation<Routes.AuthGraph>(
            startDestination = Routes.SignInScreen
        ) {
            //로그인 화면
            composable<Routes.SignInScreen> {
                SignInScreen(
//                        modifier = Modifier.padding(innerPadding),
                    viewModel = authViewModel,
                    onEvent = authViewModel::onEvent,
                    onNavigateToSignUp = { navController.navigate(Routes.SignUpScreen) },
                    onNavigateToHome = {
                        navController.navigate(Routes.MainGraph) {
                            popUpTo(Routes.AuthGraph) { inclusive = true }
                        }
                    }
                )
            }

            //회원가입 화면
            composable<Routes.SignUpScreen> {
                SignUpScreen(
//                        modifier = Modifier.padding(innerPadding),
                    viewModel = authViewModel,
                    onEvent = authViewModel::onEvent,
                    onNavigateToSignIn = { navController.popBackStack() },
                    onNavigateToHome = {
                        navController.navigate(Routes.MainGraph) {
                            popUpTo(Routes.AuthGraph) { inclusive = true }
                        }
                    }
                )
            }
        }

        //메인 네비게이션
        composable<Routes.MainGraph> {
            val currentUser = (authState as? AuthState.Authenticated)?.user

            if (currentUser != null) {
                MainNavigation(
                    currentUser = currentUser,
                    onSignOut = {
                        navController.navigate(Routes.SignInScreen) {
                            popUpTo(Routes.MainGraph) { inclusive = true }
                        }

                        //로그아웃 처리
                        authViewModel.onEvent(AuthEvent.SignOut)
                    },
                    parentNavController = navController,
                )
            } else {
                // 로그인된 정보가 없다면 로그인 화면으로 강제 이동
                LaunchedEffect(Unit) {
                    navController.navigate(Routes.SignInScreen) {
                        popUpTo(Routes.MainGraph) { inclusive = true }
                    }
                }
            }
        }
    }
}