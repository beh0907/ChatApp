package com.skymilk.chatapp.store.presentation.utils

import android.Manifest.permission.POST_NOTIFICATIONS
import android.os.Build
import com.gun0912.tedpermission.coroutine.TedPermission

object PermissionUtil {
    suspend fun requestNotificationPermission(): Boolean {
        //티라미수 이하는 권한이 필요하지 않다
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return true

        //권한 요청 및 결과 정보 리턴
        return TedPermission.create()
            .setPermissions(POST_NOTIFICATIONS)
            .checkGranted()
    }
}