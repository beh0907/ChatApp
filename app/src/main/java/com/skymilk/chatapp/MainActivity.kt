package com.skymilk.chatapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.google.firebase.messaging.FirebaseMessaging
import com.skymilk.chatapp.store.presentation.common.CustomAlertDialog
import com.skymilk.chatapp.store.presentation.navigation.graph.AppNavigation
import com.skymilk.chatapp.store.presentation.utils.Event
import com.skymilk.chatapp.store.presentation.utils.EventBus.events
import com.skymilk.chatapp.store.presentation.utils.PermissionUtil
import com.skymilk.chatapp.ui.theme.ChatAppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var firebaseMessaging: FirebaseMessaging

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()

        setContent {
            val mainViewModel: MainViewModel = hiltViewModel()

            ChatAppTheme(viewModel = mainViewModel) {
                //배터리 최적화 알림 무시 여부 체크
                val ignoringOptimizationState by mainViewModel.ignoringOptimizationState.collectAsStateWithLifecycle()

                // 딥링크 데이터 상태 관리
                var deepLinkData by remember { mutableStateOf(intent.data) }

                //메시지 수집 및 처리
                SetObserveMessage()

                //권한 요청
                LaunchedEffect(Unit) {
                    PermissionUtil.requestAllPermissions()
                }

                //배터리 최적화 비활성화 요청
                RequestIgnoringOptimization(
                    ignoringOptimizationState = ignoringOptimizationState,
                    onRefuseIgnoringOptimization = {
                        mainViewModel.onEvent(MainEvent.SetRefuseIgnoringOptimization)
                    }
                )

                //네비게이션 화면
                AppNavigation(
                    isDeepLink = deepLinkData != null
                )
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
                    }
                }
            }
        }
    }

    @Composable
    private fun RequestIgnoringOptimization(
        ignoringOptimizationState: Boolean,
        onRefuseIgnoringOptimization: () -> Unit
    ) {
        var visibleRequestDialog by rememberSaveable { mutableStateOf(true) }

        if (visibleRequestDialog && !ignoringOptimizationState) {
            CustomAlertDialog(
                title = "권한이 필요합니다",
                message = "알림 메시지를 정삭적으로 수신하기 위해서 배터리 사용량 최적화 목록에서 제외하는 권한이 필요합니다.\n\n 계속 하시겠습니까?",
                dismissText = "다시 보지 않기",
                onConfirm = {
                    // 1. 배터리 최적화 허용 여부 확인,
                    val isIgnoringBatteryOptimizations =
                        (getSystemService(POWER_SERVICE) as PowerManager).isIgnoringBatteryOptimizations(
                            packageName
                        )

                    //false면 최적화 기능 사용 중
                    if (!isIgnoringBatteryOptimizations) {
                        val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
                        intent.data = Uri.parse("package:$packageName")
                        startActivity(intent)
                    }
                },
                onDismiss = {
                    onRefuseIgnoringOptimization()
                    visibleRequestDialog = false
                }
            )
        }
    }
}

