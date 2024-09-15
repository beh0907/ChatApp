package com.skymilk.chatapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.skymilk.chatapp.store.presentation.screen.AuthState
import com.skymilk.chatapp.store.presentation.screen.auth.AuthViewModel
import com.skymilk.chatapp.store.presentation.screen.auth.signIn.SignInScreen
import com.skymilk.chatapp.store.presentation.screen.auth.signUp.SignUpScreen
import com.skymilk.chatapp.store.presentation.screen.main.home.HomeScreen
import com.skymilk.chatapp.store.presentation.screen.navigation.Screens
import com.skymilk.chatapp.ui.theme.ChatAppTheme
import com.skymilk.chatapp.utils.Event
import com.skymilk.chatapp.utils.EventBus.events
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            ChatAppTheme {

                //메시지 수집 및 처리
                SetObserveMessage()

                //네비게이션 화면
                NavGraph()
            }
        }
    }

    @Composable
    private fun SetObserveMessage() {
        //생명주기
        val lifecycle = LocalLifecycleOwner.current.lifecycle
        LaunchedEffect(key1 = lifecycle) {
            //앱이 실행중일 경우에만 수집한다
            repeatOnLifecycle(state = Lifecycle.State.STARTED) {
                //알림 이벤트 수집 시 처리
                events.collectLatest { event ->
                    when (event) {
                        is Event.Toast -> {
                            Toast.makeText(
                                this@MainActivity,
                                event.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        is Event.SnackBar -> {}
                        is Event.Dialog -> {}
                        else -> Unit
                    }
                }
            }
        }
    }

    @Composable
    private fun NavGraph() {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            //계정 처리 뷰모델 및 계정 상태
            val authViewModel: AuthViewModel by viewModels()
            val authState by authViewModel.authState.collectAsStateWithLifecycle()

            //화면 네비게이션 설정
            val navController = rememberNavController()
            val startDestination = when (authState) {
                is AuthState.Authenticated -> Screens.Home.name
                else -> Screens.SignIn.name
            }

            NavHost(navController = navController, startDestination = startDestination) {

                composable(Screens.SignIn.name) {
                    SignInScreen(
                        modifier = Modifier.padding(innerPadding),
                        viewModel = authViewModel,
                        onNavigateToSignUp = { navController.navigate(Screens.SignUp.name) },
                        onNavigateToHome = {
                            navController.navigateUp()
                            navController.navigate(Screens.Home.name)
                        }
                    )
                }

                composable(Screens.SignUp.name) {
                    SignUpScreen(
                        modifier = Modifier.padding(innerPadding),
                        viewModel = authViewModel,
                        onNavigateToSignIn = {navController.navigate(Screens.SignIn.name)},
                        onNavigateToHome = {
                            navController.navigateUp()
                            navController.navigate(Screens.Home.name)
                        }
                    )
                }

                composable(Screens.Home.name) {
                    HomeScreen(
                        modifier = Modifier.padding(innerPadding),
                        viewModel = authViewModel,
                        onSignOut = {
                            //로그아웃
                            authViewModel.signOut()

                            //화면 이동
                            navController.navigateUp()
                            navController.navigate(Screens.SignIn.name)
                        }
                    )
                }
            }
        }
    }
}