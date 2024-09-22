package com.skymilk.chatapp.store.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.skymilk.chatapp.store.presentation.navigation.routes.AuthNavigation
import com.skymilk.chatapp.store.presentation.navigation.routes.Navigations
import com.skymilk.chatapp.store.presentation.navigation.routes.StartNavigation
import com.skymilk.chatapp.store.presentation.screen.auth.AuthState
import com.skymilk.chatapp.store.presentation.screen.auth.AuthViewModel
import com.skymilk.chatapp.store.presentation.screen.auth.signIn.SignInScreen
import com.skymilk.chatapp.store.presentation.screen.auth.signUp.SignUpScreen
import com.skymilk.chatapp.store.presentation.screen.splash.SplashScreen

@Composable
fun AppNavigation() {
    //계정 처리 뷰모델 및 계정 상태
    val authViewModel: AuthViewModel = hiltViewModel()
    val authState by authViewModel.authState.collectAsStateWithLifecycle()

    //화면 네비게이션 설정
    val navController = rememberNavController()
    val startDestination =
        if (authState is AuthState.Authenticated) Navigations.Main.route else Navigations.Auth.route

    NavHost(
        navController = navController,
        startDestination = Navigations.Start.route
    ) {
        //시작 네비게이션
        navigation(
            route = Navigations.Start.route,
            startDestination = StartNavigation.SplashScreen.route
        ) {
            //로딩 화면
            composable(StartNavigation.SplashScreen.route) {
                SplashScreen(
                    onAnimationFinished = {
                        //완료 후 시작 화면 이동
                        navController.navigate(startDestination) {
                            popUpTo(Navigations.Start.route) { inclusive = false }
                        }
                    })
            }
        }

        //인증 네비게이션
        navigation(
            route = Navigations.Auth.route,
            startDestination = AuthNavigation.SignInScreen.route
        ) {
            //로그인 화면
            composable(AuthNavigation.SignInScreen.route) {
                SignInScreen(
//                        modifier = Modifier.padding(innerPadding),
                    viewModel = authViewModel,
                    onNavigateToSignUp = { navController.navigate(AuthNavigation.SignUpScreen.route) },
                    onNavigateToHome = {
                        navController.navigate(Navigations.Main.route) {
                            popUpTo(Navigations.Auth.route) { inclusive = true }
                        }
                    }
                )
            }

            //회원가입 화면
            composable(AuthNavigation.SignUpScreen.route) {
                SignUpScreen(
//                        modifier = Modifier.padding(innerPadding),
                    viewModel = authViewModel,
                    onNavigateToSignIn = { navController.popBackStack() },
                    onNavigateToHome = {
                        navController.navigate(Navigations.Main.route) {
                            popUpTo(Navigations.Auth.route) { inclusive = true }
                        }
                    }
                )
            }
        }

        //메인 네비게이션
        composable(Navigations.Main.route) {
            val currentUser = (authState as? AuthState.Authenticated)?.user
            if (currentUser != null) {
                MainContent(
                    currentUser = currentUser,
                    onSignOut = {
                        navController.navigate(AuthNavigation.SignInScreen.route) {
                            popUpTo(Navigations.Main.route) { inclusive = true }
                        }

                        //로그아웃 처리
                        authViewModel.signOut()
                    },
                    parentNavController = navController
                )
            } else {
                // 로그인된 정보가 없다면 로그인 화면으로 강제 이동
                LaunchedEffect(Unit) {
                    navController.navigate(AuthNavigation.SignInScreen.route) {
                        popUpTo(Navigations.Main.route) { inclusive = true }
                    }
                }
            }
        }
    }
}