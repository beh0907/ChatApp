package com.skymilk.chatapp.utils

import android.Manifest.permission.CAMERA
import android.Manifest.permission.POST_NOTIFICATIONS
import android.os.Build
import com.gun0912.tedpermission.coroutine.TedPermission

object PermissionUtil {
    suspend fun requestAllPermissions(): Boolean {
        val permissions = mutableListOf(
            CAMERA
        )

        //33이상은 POST_NOTIFICATIONS을 추가한다
        if (Build.VERSION.SDK_INT >= 33) permissions.add(POST_NOTIFICATIONS)

        //권한 요청 및 결과 정보 리턴
        return TedPermission.create()
            .setPermissions(*permissions.toTypedArray())
            .checkGranted()
    }

}