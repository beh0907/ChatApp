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
        if (authState is AuthState.Authenticated) Navigations.Main else Navigations.Auth

    NavHost(
        navController = navController,
        startDestination = Navigations.Start
    ) {
        //시작 네비게이션
        navigation<Navigations.Start>(
            startDestination = StartNavigation.SplashScreen
        ) {
            //로딩 화면
            composable<StartNavigation.SplashScreen> {
                SplashScreen(
                    onAnimationFinished = {
                        //완료 후 시작 화면 이동
                        navController.navigate(startDestination) {
                            popUpTo(Navigations.Start) { inclusive = false }
                        }
                    })
            }
        }

        //인증 네비게이션
        navigation<Navigations.Auth>(
            startDestination = AuthNavigation.SignInScreen
        ) {
            //로그인 화면
            composable<AuthNavigation.SignInScreen> {
                SignInScreen(
//                        modifier = Modifier.padding(innerPadding),
                    viewModel = authViewModel,
                    onNavigateToSignUp = { navController.navigate(AuthNavigation.SignUpScreen) },
                    onNavigateToHome = {
                        navController.navigate(Navigations.Main) {
                            popUpTo(Navigations.Auth) { inclusive = true }
                        }
                    }
                )
            }

            //회원가입 화면
            composable<AuthNavigation.SignUpScreen> {
                SignUpScreen(
//                        modifier = Modifier.padding(innerPadding),
                    viewModel = authViewModel,
                    onNavigateToSignIn = { navController.popBackStack() },
                    onNavigateToHome = {
                        navController.navigate(Navigations.Main) {
                            popUpTo(Navigations.Auth) { inclusive = true }
                        }
                    }
                )
            }
        }

        //메인 네비게이션
        composable<Navigations.Main> {
            val currentUser = (authState as? AuthState.Authenticated)?.user
            if (currentUser != null) {
                MainContent(
                    currentUser = currentUser,
                    onSignOut = {
                        navController.navigate(AuthNavigation.SignInScreen) {
                            popUpTo(Navigations.Main) { inclusive = true }
                        }

                        //로그아웃 처리
                        authViewModel.signOut()
                    },
                    parentNavController = navController
                )
            } else {
                // 로그인된 정보가 없다면 로그인 화면으로 강제 이동
                LaunchedEffect(Unit) {
                    navController.navigate(AuthNavigation.SignInScreen) {
                        popUpTo(Navigations.Main) { inclusive = true }
                    }
                }
            }
        }
    }
}