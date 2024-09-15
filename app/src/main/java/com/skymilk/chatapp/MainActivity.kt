package com.skymilk.chatapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.skymilk.chatapp.store.presentation.screen.AuthState
import com.skymilk.chatapp.store.presentation.screen.auth.AuthViewModel
import com.skymilk.chatapp.store.presentation.screen.auth.signIn.SignInScreen
import com.skymilk.chatapp.store.presentation.screen.main.home.HomeScreen
import com.skymilk.chatapp.store.presentation.screen.navigation.Screens
import com.skymilk.chatapp.ui.theme.ChatAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            ChatAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    val navController = rememberNavController()
                    val authState by authViewModel.authState.collectAsStateWithLifecycle()

                    val startDestination = when (authState) {
                        is AuthState.Authenticated -> Screens.Home.name
                        else -> Screens.SignIn.name
                    }


                    NavHost(navController = navController, startDestination = startDestination) {
                        composable(Screens.SignIn.name) {
                            SignInScreen(
                                modifier = Modifier.padding(innerPadding),
                                onNavigateToSignUp = { navController.navigate(Screens.SignUp.name) },
                                onNavigateToHome = {
                                    navController.navigate(Screens.Home.name) {
                                        popUpTo(Screens.SignIn.name) { inclusive = true }
                                    }
                                }
                            )
                        }

                        composable(Screens.SignUp.name) {

                        }

                        composable(Screens.Home.name) {
                            HomeScreen(
                                modifier = Modifier.padding(innerPadding),
                                onSignOut = {
                                    // 로그아웃 로직
                                    navController.navigate(Screens.SignIn.name) {
                                        popUpTo(Screens.Home.name) { inclusive = true }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}