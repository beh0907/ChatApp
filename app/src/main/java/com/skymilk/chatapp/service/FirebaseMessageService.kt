package com.skymilk.chatapp.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Intent
import android.os.PowerManager
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import androidx.core.content.getSystemService
import androidx.core.net.toUri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.skymilk.chatapp.MainActivity
import com.skymilk.chatapp.R
import com.skymilk.chatapp.store.domain.usecase.chatRoomSetting.ChatRoomSettingUseCases
import com.skymilk.chatapp.store.domain.usecase.navigation.NavigationUseCases
import com.skymilk.chatapp.store.domain.usecase.user.UserUseCases
import com.skymilk.chatapp.store.domain.usecase.userSetting.UserSettingUseCases
import com.skymilk.chatapp.store.presentation.navigation.routes.Routes
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.random.Random

@AndroidEntryPoint
class FirebaseMessageService : FirebaseMessagingService() {

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    @Inject
    lateinit var chatRoomSettingUseCases: ChatRoomSettingUseCases

    @Inject
    lateinit var userSettingUseCases: UserSettingUseCases

    @Inject
    lateinit var navigationUseCases: NavigationUseCases

    @Inject
    lateinit var userUseCases: UserUseCases

    // 코루틴 스코프 추가
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    // Wake Lock 추가
    private var wakeLock: PowerManager.WakeLock? = null

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        firebaseAuth.currentUser?.let { user ->
            userUseCases.updateFcmToken(user.uid, token)
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        //알림 텍스트
        val title = message.notification?.title ?: message.data["title"]
        val body = message.notification?.body ?: message.data["body"]

        // Wake Lock 획득
        acquireWakeLock()

        // 코루틴 스코프에서 실행
        scope.launch {
            try {
                showNotification(title, body, message.data)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                // Wake Lock 해제
                releaseWakeLock()
            }
        }
    }


    private fun acquireWakeLock() {
        if (wakeLock == null) {
            val powerManager = getSystemService(POWER_SERVICE) as PowerManager
            wakeLock = powerManager.newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK,
                "FirebaseMessageService:WakeLock"
            )
            wakeLock?.acquire(10 * 60 * 1000L /*10 minutes*/)
        }
    }

    private fun releaseWakeLock() {
        if (wakeLock?.isHeld == true) {
            wakeLock?.release()
        }
        wakeLock = null
    }

    private suspend fun showNotification(
        title: String? = "",
        message: String? = "",
        messageData: MutableMap<String, String>
    ) {
        val senderId = messageData["senderId"] ?: return
        val chatRoomId = messageData["chatRoomId"] ?: return

        //로그인한 유저만 알림 발생
        if (firebaseAuth.currentUser == null) return

        //전송한 유저와 일치하지 않은 유저
        if (firebaseAuth.currentUser?.uid == senderId) return

        //유저 알람 설정 여부
        if (!userSettingUseCases.getUserAlarmSetting().first()) return

        // 채팅방 알람 비활설화 여부 확인
        if (chatRoomSettingUseCases.getAlarmSetting(chatRoomId).first()) return

        //현재 화면이 알람이 발생한 채팅방과 동일한 채팅방인지 체크
        val navigationState = navigationUseCases.getCurrentDestination().first()
        if (navigationState.destination == Routes.ChatRoomScreen.javaClass.toString()
            && navigationState.params["chatRoomId"] == chatRoomId
        ) return

        withContext(Dispatchers.Main) {
            val notificationManager = getSystemService<NotificationManager>()!!

            val channel =
                NotificationChannel("messages", "메시지 알림", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)

            //딥링크 전송
            val activityIntent =
                Intent(this@FirebaseMessageService, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    this.data = "chatapp://chatrooms/$chatRoomId".toUri()
                }

            val pendingIntent = TaskStackBuilder.create(this@FirebaseMessageService).run {
                addNextIntentWithParentStack(activityIntent)
                getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE or FLAG_UPDATE_CURRENT)
            }

            val notificationId = Random.nextInt(1000)
            val notification = NotificationCompat.Builder(this@FirebaseMessageService, "messages")
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)  // 알림 클릭 시 인텐트 실행
                .build()

            notificationManager.notify(notificationId, notification)
        }
    }

    // 서비스가 종료될 때 코루틴도 취소
    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}