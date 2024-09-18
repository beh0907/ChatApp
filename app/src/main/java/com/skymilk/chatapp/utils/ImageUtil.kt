package com.skymilk.chatapp.utils

import android.content.Context
import android.os.Environment
import java.io.File
import java.util.UUID

object ImageUtil {
    // 이미지 파일 생성
    fun createImageFile(context: Context): File {
        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("${System.currentTimeMillis()} ${UUID.randomUUID()}", ".jpg", storageDir)
    }
}