package com.skymilk.chatapp.store.presentation.screen.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.skymilk.chatapp.store.presentation.screen.auth.AuthState
import com.skymilk.chatapp.store.presentation.screen.auth.AuthViewModel
import com.skymilk.chatapp.store.presentation.screen.auth.signIn.SignInScreen
import com.skymilk.chatapp.store.presentation.screen.auth.signUp.SignUpScreen


@Composable
fun NavGraph() {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        //계정 처리 뷰모델 및 계정 상태
        val authViewModel: AuthViewModel = hiltViewModel()
        val authState by authViewModel.authState.collectAsStateWithLifecycle()

        //화면 네비게이션 설정
        val navController = rememberNavController()
        val startDestination =
            if (authState is AuthState.Authenticated) Screens.MainScreen.route else Screens.SignInScreen.route

        NavHost(
            navController = navController,
            startDestination = startDestination
        ) {
            //로그인 화면
            composable(Screens.SignInScreen.route) {
                SignInScreen(
                    modifier = Modifier.padding(innerPadding),
                    viewModel = authViewModel,
                    onNavigateToSignUp = { navController.navigate(Screens.SignUpScreen.route) },
                    onNavigateToHome = {
                        navController.navigate(Screens.MainScreen.route) {
                            popUpTo(Screens.SignInScreen.route) { inclusive = true }
                        }
                    }
                )
            }

            //회원가입 화면
            composable(Screens.SignUpScreen.route) {
                SignUpScreen(
                    modifier = Modifier.padding(innerPadding),
                    viewModel = authViewModel,
                    onNavigateToSignIn = { navController.popBackStack() },
                    onNavigateToHome = {
                        navController.navigate(Screens.MainScreen.route) {
                            popUpTo(Screens.SignUpScreen.route) { inclusive = true }
                        }
                    }
                )
            }

            //로그인 후 메인화면
            composable(Screens.MainScreen.route) {
                val currentUser = (authState as? AuthState.Authenticated)?.user
                if (currentUser != null) {
                    MainNavGraph(
                        currentUser = currentUser,
                        onSignOut = {
                            //로그아웃 처리
                            authViewModel.signOut()

                            navController.navigate(Screens.SignInScreen.route) {
                                popUpTo(Screens.MainScreen.route) { inclusive = true }
                            }
                        },
                        onNavigateToChatRoom = {}
                    )
                } else {
                    // 로그인된 정보가 없다면 로그인 화면으로 강제 이동
                    LaunchedEffect(Unit) {
                        navController.navigate(Screens.SignInScreen.route) {
                            popUpTo(Screens.MainScreen.route) { inclusive = true }
                        }
                    }
                }
            }
        }
    }
}