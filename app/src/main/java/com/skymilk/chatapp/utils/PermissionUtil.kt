package com.skymilk.chatapp.utils

import android.Manifest.permission.CAMERA
import android.Manifest.permission.INTERNET
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
            permissions.add(READ_EXTERNAL_STORAGE)
        } else {
            permissions.add(READ_MEDIA_IMAGES)
        }

        //권한 요청 및 결과 정보 리턴
        return TedPermission.create()
            .setPermissions(*permissions.toTypedArray())
            .checkGranted()
    }

    //코루틴 권한 요청 처리
    suspend fun requestStoragePermissions(): Boolean {
        // SDK 버전에 따라 추가 권한을 요청
        val permissions = if (Build.VERSION.SDK_INT <= 32) {
            listOf(READ_EXTERNAL_STORAGE)
        } else if (Build.VERSION.SDK_INT >= 34)
            listOf(READ_MEDIA_IMAGES, READ_MEDIA_VIDEO, READ_MEDIA_VISUAL_USER_SELECTED)
        else {
            listOf(READ_MEDIA_IMAGES, READ_MEDIA_VIDEO)
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