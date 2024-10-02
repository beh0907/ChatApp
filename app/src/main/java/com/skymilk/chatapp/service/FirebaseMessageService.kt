package com.skymilk.chatapp.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import androidx.core.content.getSystemService
import androidx.core.net.toUri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.skymilk.chatapp.MainActivity
import com.skymilk.chatapp.R
import com.skymilk.chatapp.store.domain.usecase.setting.SettingUseCases
import com.skymilk.chatapp.store.domain.usecase.user.UserUseCases
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject
import kotlin.random.Random

@AndroidEntryPoint
class FirebaseMessageService : FirebaseMessagingService() {

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    @Inject
    lateinit var settingUseCases: SettingUseCases

    @Inject
    lateinit var userUseCases: UserUseCases


    override fun onNewToken(token: String) {
        super.onNewToken(token)

        // 현재 로그인된 사용자가 있는 경우에만 토큰 업데이트
        firebaseAuth.currentUser?.let { user ->
            userUseCases.updateFcmToken(user.uid, token)
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val title = message.notification?.title ?: message.data["title"]
        val body = message.notification?.body ?: message.data["body"]
        showNotification(title, body, message.data)
    }

    private fun showNotification(
        title: String? = "",
        message: String? = "",
        messageData: MutableMap<String, String>
    ) {
        val senderId = messageData["senderId"] ?: return
        val chatRoomId = messageData["chatRoomId"] ?: return

        //로그인 상태가 아니라면 알림X
        if (firebaseAuth.currentUser == null) return

        //내가 보낸 메시지라면 알림X
        if (firebaseAuth.currentUser?.uid == senderId) return

        //알림이 해제된 채팅방이라면 알림X
        if (settingUseCases.getAlarmSetting(chatRoomId)) return

        val notificationManager = getSystemService<NotificationManager>()!!

        //알람 채널 생성
        val channel = NotificationChannel("messages", "메시지 알림", NotificationManager.IMPORTANCE_HIGH)
        notificationManager.createNotificationChannel(channel)

        // 알림 클릭 시 MainActivity를 열고 채팅방 ID 전달
        val activityIntent = Intent(this, MainActivity::class.java).apply {
            this.data = "chatapp://chatrooms/$chatRoomId".toUri()
        }

        val pendingIntent = TaskStackBuilder.create(this).run {
            addNextIntentWithParentStack(activityIntent)
            getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE)
        }

        val notificationId = Random.nextInt(1000)
        val notification = NotificationCompat.Builder(this, "messages")
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.mipmap.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)  // 알림 클릭 시 인텐트 실행
            .build()

        notificationManager.notify(notificationId, notification)
    }
}