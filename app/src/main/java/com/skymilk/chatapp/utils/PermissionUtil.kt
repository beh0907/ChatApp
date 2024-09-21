package com.skymilk.chatapp.utils

import android.Manifest.permission.CAMERA
import android.Manifest.permission.INTERNET
import android.Manifest.permission.POST_NOTIFICATIONS
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.Manifest.permission.READ_MEDIA_VIDEO
import android.Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
import android.os.Build
import com.gun0912.tedpermission.coroutine.TedPermission

object PermissionUtil {
    suspend fun requestAllPermissions(): Boolean {
        val permissions = mutableListOf(
            CAMERA,
            INTERNET
        )

        // SDK 버전에 따라 추가 권한을 요청
        if (Build.VERSION.SDK_INT <= 32) {
            permissions.addAll(listOf(READ_EXTERNAL_STORAGE))
        } else {
            permissions.addAll(listOf(READ_MEDIA_IMAGES, READ_MEDIA_VIDEO))

            //34이상은 POST_NOTIFICATIONS을 추가한다
            if (Build.VERSION.SDK_INT >= 34) permissions.add(POST_NOTIFICATIONS)

            //34이상은 READ_MEDIA_VISUAL_USER_SELECTED를 추가한다
            if (Build.VERSION.SDK_INT >= 34) permissions.add(READ_MEDIA_VISUAL_USER_SELECTED)
        }

        //권한 요청 및 결과 정보 리턴
        return TedPermission.create()
            .setPermissions(*permissions.toTypedArray())
            .checkGranted()
    }

    //코루틴 권한 요청 처리
    suspend fun requestStoragePermissions(): Boolean {
        val permissions = mutableListOf<String>()

        // SDK 버전에 따라 추가 권한을 요청
        // SDK 버전에 따라 추가 권한을 요청
        if (Build.VERSION.SDK_INT <= 32) {
            permissions.addAll(listOf(READ_EXTERNAL_STORAGE))
        } else {
            permissions.addAll(listOf(READ_MEDIA_IMAGES, READ_MEDIA_VIDEO))

            //34이상은 READ_MEDIA_VISUAL_USER_SELECTED를 더 추가한다
            if (Build.VERSION.SDK_INT >= 34) permissions.add(READ_MEDIA_VISUAL_USER_SELECTED)
        }

        //권한 요청 및 결과 정보 리턴
        return TedPermission.create()
            .setPermissions(*permissions.toTypedArray())
            .checkGranted()
    }

    //코루틴 권한 요청 처리
    suspend fun requestCameraPermissions(): Boolean {
        val permissions = listOf(
            CAMERA
        )

        //권한 요청 및 결과 정보 리턴
        return TedPermission.create()
            .setPermissions(*permissions.toTypedArray())
            .checkGranted()
    }

}