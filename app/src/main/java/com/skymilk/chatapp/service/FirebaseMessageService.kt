package com.skymilk.chatapp.service

import android.app.ActivityManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.net.Uri
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.getSystemService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.skymilk.chatapp.MainActivity
import com.skymilk.chatapp.R
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.random.Random

@AndroidEntryPoint
class FirebaseMessageService : FirebaseMessagingService() {

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        //받은 메시지는 알림 출력
//        message.notification?.let {
//            showNotification(it.title, it.body, message.data)
//        }

        val title = message.notification?.title ?: message.data["title"]
        val body = message.notification?.body ?: message.data["body"]
        showNotification(title, body, message.data)
    }

    private fun showNotification(
        title: String? = "",
        message: String? = "",
        data: MutableMap<String, String>
    ) {
        //로그인 상태가 아니라면 알림X
        if (firebaseAuth.currentUser == null) return

        //내가 보낸 메시지라면 알림X
        if (firebaseAuth.currentUser?.uid == data["senderId"]) return

        val notificationManager = getSystemService<NotificationManager>()!!

        //알람 채널 생성
        val channel = NotificationChannel("messages", "메시지 알림", NotificationManager.IMPORTANCE_HIGH)
        notificationManager.createNotificationChannel(channel)

        // 알림 클릭 시 MainActivity를 열고 채팅방 ID 전달
        val deepLinkUri = "chatapp://chatrooms/${data["chatRoomId"]}"

        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(deepLinkUri)).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("chatRoomId", data["chatRoomId"])
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

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

    private fun isAppRunning(): Boolean {
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningAppProcesses = activityManager.runningAppProcesses ?: return false
        return runningAppProcesses.any { it.processName == packageName && it.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND }
    }

}