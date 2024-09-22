package com.skymilk.chatapp.store.presentation.navigation

import android.content.Intent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.skymilk.chatapp.store.presentation.screen.auth.AuthState
import com.skymilk.chatapp.store.presentation.screen.auth.AuthViewModel
import com.skymilk.chatapp.store.presentation.screen.auth.signIn.SignInScreen
import com.skymilk.chatapp.store.presentation.screen.auth.signUp.SignUpScreen
import com.skymilk.chatapp.store.presentation.screen.splash.SplashScreen


@Composable
fun NavGraph() {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        //계정 처리 뷰모델 및 계정 상태
        val authViewModel: AuthViewModel = hiltViewModel()
        val authState by authViewModel.authState.collectAsStateWithLifecycle()

        //화면 네비게이션 설정
        val navController = rememberNavController()
        val startDestination =
            if (authState is AuthState.Authenticated) Routes.MainNavigation.route else Routes.AuthNavigation.route

        NavHost(
            navController = navController,
            startDestination = Routes.StartNavigation.route
        ) {
            navigation(
                route = Routes.StartNavigation.route,
                startDestination = Routes.SplashScreen.route
            ) {
                //로딩 화면
                composable(Routes.SplashScreen.route) {
                    SplashScreen(onAnimationFinished = {
                        //완료 후 시작 화면 이동
                        navController.navigate(startDestination) {
                            popUpTo(Routes.StartNavigation.route) { inclusive = false }
                        }
                    })
                }
            }

            navigation(
                route = Routes.AuthNavigation.route,
                startDestination = Routes.SignInScreen.route
            ) {
                //로그인 화면
                composable(Routes.SignInScreen.route) {
                    SignInScreen(
                        modifier = Modifier.padding(innerPadding),
                        viewModel = authViewModel,
                        onNavigateToSignUp = { navController.navigate(Routes.SignUpScreen.route) },
                        onNavigateToHome = {
                            navController.navigate(Routes.MainNavigation.route) {
                                popUpTo(Routes.AuthNavigation.route) { inclusive = true }
                            }
                        }
                    )
                }

                //회원가입 화면
                composable(Routes.SignUpScreen.route) {
                    SignUpScreen(
                        modifier = Modifier.padding(innerPadding),
                        viewModel = authViewModel,
                        onNavigateToSignIn = { navController.popBackStack() },
                        onNavigateToHome = {
                            navController.navigate(Routes.MainNavigation.route) {
                                popUpTo(Routes.AuthNavigation.route) { inclusive = true }
                            }
                        }
                    )
                }
            }

            //메인 네비게이션
            navigation(
                route = Routes.MainNavigation.route,
                startDestination = Routes.MainScreen.route,
            ) {

                //로그인 후 메인화면
                composable(Routes.MainScreen.route) {
                    val currentUser = (authState as? AuthState.Authenticated)?.user
                    if (currentUser != null) {
                        MainNavGraph(
                            currentUser = currentUser,
                            onSignOut = {
                                navController.navigate(Routes.SignInScreen.route) {
                                    popUpTo(Routes.MainScreen.route) { inclusive = true }
                                }

                                //로그아웃 처리
                                authViewModel.signOut()
                            }
                        )
                    } else {
                        // 로그인된 정보가 없다면 로그인 화면으로 강제 이동
                        LaunchedEffect(Unit) {
                            navController.navigate(Routes.SignInScreen.route) {
                                popUpTo(Routes.MainScreen.route) { inclusive = true }
                            }
                        }
                    }
                }
            }

        }
    }
}