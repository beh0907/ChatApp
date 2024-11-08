package com.skymilk.chatapp.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.PowerManager
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import androidx.core.content.getSystemService
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.net.toUri
import coil.ImageLoader
import coil.request.ImageRequest
import coil.size.Scale
import coil.size.Size
import coil.transform.Transformation
import com.commit451.coiltransformations.CropTransformation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.skymilk.chatapp.MainActivity
import com.skymilk.chatapp.R
import com.skymilk.chatapp.store.domain.usecase.chatRoomSetting.ChatRoomSettingUseCases
import com.skymilk.chatapp.store.domain.usecase.shared.SharedUseCases
import com.skymilk.chatapp.store.domain.usecase.user.UserUseCases
import com.skymilk.chatapp.store.domain.usecase.userSetting.UserSettingUseCases
import com.skymilk.chatapp.store.presentation.common.SquircleTransformation
import com.skymilk.chatapp.store.presentation.navigation.routes.Routes
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
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
    lateinit var sharedUseCases: SharedUseCases

    @Inject
    lateinit var userUseCases: UserUseCases

    // Job을 따로 보관하여 관리
    private var serviceJob = SupervisorJob()

    // scope를 lazy하게 초기화하여 서비스 생명주기에 맞춤
    private val scope by lazy {
        CoroutineScope(serviceJob + Dispatchers.IO)
    }

    // Wake Lock 추가
    private var wakeLock: PowerManager.WakeLock? = null

    override fun onCreate() {
        super.onCreate()
        // 서비스가 생성될 때 새로운 Job 생성
        serviceJob = SupervisorJob()
    }

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
            wakeLock?.acquire(20 * 1000L /* 20 초*/)
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
        val largeIcon = messageData["largeIcon"] ?: ""

        //로그인한 유저만 알림 발생
        if (firebaseAuth.currentUser == null) return

        //전송한 유저와 일치하지 않은 유저
        if (firebaseAuth.currentUser?.uid == senderId) return

        //유저 알람 설정 여부
        if (!userSettingUseCases.getUserAlarmSetting().first()) return

        // 채팅방 알람 비활설화 여부 확인
        if (chatRoomSettingUseCases.getAlarmSetting(chatRoomId).first()) return

        //현재 화면이 알람이 발생한 채팅방과 동일한 채팅방인지 체크
        val navigationState = sharedUseCases.getCurrentDestination()
        if (navigationState.destination == Routes.ChatRoomScreen.javaClass.toString()
            && navigationState.params["chatRoomId"] == chatRoomId
        ) return

        withContext(Dispatchers.Main) {
            val notificationManager = getSystemService<NotificationManager>()!!
            val imageSize = 256

            // 기본 이미지를 Bitmap으로 변환
            val defaultIcon =
                ResourcesCompat.getDrawable(resources, R.drawable.bg_default_profile, null)
                    ?.toBitmap()?.let { bitmap ->
                        SquircleTransformation().transform(
                            bitmap,
                            Size(imageSize, imageSize)
                        )
                    }


            // 프로필 이미지 로드
            val largeIcon = try {
                if (largeIcon.isBlank()) defaultIcon
                else {
                    // Coil을 사용하여 URL 이미지 로드
                    ImageLoader(applicationContext).execute(
                        ImageRequest.Builder(applicationContext)
                            .data(largeIcon)
                            .size(imageSize) // 적절한 크기로 조정
                            .scale(Scale.FIT) // 이미지를 지정된 크기로 확대/축소
                            .transformations(
                                CropTransformation(),
                                SquircleTransformation()
                            )
                            .fallback(R.drawable.bg_default_profile)
                            .error(R.drawable.bg_default_profile)
                            .allowHardware(false) // NotificationManager에서 하드웨어 비트맵 사용 불가
                            .build()
                    ).drawable?.toBitmap()
                } ?: defaultIcon
            } catch (e: Exception) {
                // 에러 발생시 기본 이미지 사용
                defaultIcon
            }

            val channel =
                NotificationChannel("messages", "메시지 알림", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)

            //딥링크 전송
            val activityIntent =
                Intent(this@FirebaseMessageService, MainActivity::class.java).apply {
                    this.data =
                        "chatapp://chatrooms/$chatRoomId?userId=${firebaseAuth.currentUser?.uid}".toUri()
                }

            val pendingIntent = TaskStackBuilder.create(this@FirebaseMessageService).run {
                addNextIntentWithParentStack(activityIntent)
                getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE)
            }

            val notificationId = Random.nextInt(1000)
            val notification = NotificationCompat.Builder(this@FirebaseMessageService, "messages")
                .setContentTitle(title)
                .setContentText(message)
                .setLargeIcon(largeIcon)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                //.setOngoing(true) // [사용자가 알림 못지우게 설정 >> 클릭해야 메시지 읽음 상태]
                .setWhen(System.currentTimeMillis()) // [알림 표시 시간 설정]
                .setShowWhen(true) // [푸시 알림 받은 시간 커스텀 설정 표시]
                .setAutoCancel(true)// [알림 클릭 시 삭제 여부]
                .setContentIntent(pendingIntent)  // 알림 클릭 시 인텐트 실행
                .setGroup(chatRoomId)
                .build()

            notificationManager.notify(notificationId, notification)
        }
    }
}