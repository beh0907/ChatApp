package com.skymilk.chatapp

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.firebase.messaging.FirebaseMessaging
import com.skymilk.chatapp.store.presentation.navigation.AppNavigation
import com.skymilk.chatapp.ui.theme.ChatAppTheme
import com.skymilk.chatapp.utils.Event
import com.skymilk.chatapp.utils.EventBus.events
import com.skymilk.chatapp.utils.PermissionUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var firebaseMessaging: FirebaseMessaging

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()

        setContent {
            ChatAppTheme {
                //권한 요청
                LaunchedEffect(Unit) {
                    PermissionUtil.requestAllPermissions()
                }

                //메시지 수집 및 처리
                SetObserveMessage()

                //네비게이션 화면
                AppNavigation(
                    isDeepLink = intent.data != null
                )
            }
        }
    }

//    fun getKeyHash() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//            val packageInfo = applicationContext.packageManager.getPackageInfo(applicationContext.packageName, PackageManager.GET_SIGNING_CERTIFICATES)
//            for (signature in packageInfo.signingInfo!!.apkContentsSigners) {
//                try {
//                    val md = MessageDigest.getInstance("SHA")
//                    md.update(signature.toByteArray())
//                    Log.d("getKeyHash", "key hash: ${Base64.encodeToString(md.digest(), Base64.NO_WRAP)}")
//                } catch (e: NoSuchAlgorithmException) {
//                    Log.w("getKeyHash", "Unable to get MessageDigest. signature=$signature", e)
//                }
//            }
//        }
//    }

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
                    }
                }
            }
        }
    }


    //다른 영역을 터치 했을 때 키보드가 내려가도록 설정
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        val inputMethodManager: InputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)

        //editText의 포커스도 제거한다
        if (currentFocus is EditText) {
            currentFocus!!.clearFocus()
        }

        return super.dispatchTouchEvent(ev)
    }
}

