package com.skymilk.chatapp.store.presentation.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import kotlin.math.log10
import kotlin.math.pow

object FileSizeUtil {
    fun formatFileSize(bytes: Long): String {
        if (bytes <= 0) return "0 B"

        val units = arrayOf("B", "KB", "MB", "GB", "TB")
        val digitGroups = (log10(bytes.toDouble()) / log10(1024.0)).toInt()

        return String.format(
            "%.1f %s",
            bytes / 1024.0.pow(digitGroups.toDouble()),
            units[digitGroups]
        )
    }

    suspend fun resizeAndCompressImages(
        context: Context,
        imageUris: List<Uri>,
        targetWidth: Int = 2000,
        targetHeight: Int = 2000,
        quality: Int = 80
    ): List<Uri> = withContext(Dispatchers.IO) {
        val resizedImageUris = mutableListOf<Uri>()
        imageUris.forEach { uri ->
            // EXIF 방향 정보 읽기 (I/O 작업)
            val orientation = context.contentResolver.openInputStream(uri)?.use { input ->
                val exif = ExifInterface(input)
                exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL
                )
            } ?: ExifInterface.ORIENTATION_NORMAL

            // 원본 비트맵 로드 (I/O 작업)
            val inputStream = context.contentResolver.openInputStream(uri)
            val originalBitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            // 비트맵 처리 작업 (CPU 집약적 작업)
            val processedBitmap = withContext(Dispatchers.Default) {
                // EXIF 방향에 따라 비트맵 회전
                val rotatedBitmap = when (orientation) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> rotateBitmap(originalBitmap, 90f)
                    ExifInterface.ORIENTATION_ROTATE_180 -> rotateBitmap(originalBitmap, 180f)
                    ExifInterface.ORIENTATION_ROTATE_270 -> rotateBitmap(originalBitmap, 270f)
                    else -> originalBitmap
                }

                // 원본 비트맵 크기 확인
                val width = rotatedBitmap.width
                val height = rotatedBitmap.height

                // 이미지 비율 유지하면서 크기 조정
                if (width <= targetWidth && height <= targetHeight) {
                    rotatedBitmap
                } else {
                    val ratioWidth = targetWidth.toFloat() / width
                    val ratioHeight = targetHeight.toFloat() / height
                    val scaleFactor = minOf(ratioWidth, ratioHeight)

                    val resizedWidth = (width * scaleFactor).toInt()
                    val resizedHeight = (height * scaleFactor).toInt()

                    Bitmap.createScaledBitmap(rotatedBitmap, resizedWidth, resizedHeight, true).also {
                        if (rotatedBitmap != originalBitmap) {
                            rotatedBitmap.recycle()
                        }
                    }
                }
            }

            // 비트맵 압축 및 파일 저장 (I/O 작업)
            val outputStream = ByteArrayOutputStream()
            processedBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)

            val resizedFile = File(context.cacheDir, "resized_${uri.lastPathSegment}")
            FileOutputStream(resizedFile).use { fileOutputStream ->
                fileOutputStream.write(outputStream.toByteArray())
            }

            // 새롭게 생성된 파일의 URI 추가
            resizedImageUris.add(Uri.fromFile(resizedFile))

            // 메모리 해제
            processedBitmap.recycle()
            originalBitmap.recycle()
        }

        return@withContext resizedImageUris
    }

    suspend fun resizeImageBitmap(
        originalImage: ImageBitmap,
        targetWidth: Int = 2000,
        targetHeight: Int = 2000,
    ): ImageBitmap = withContext(Dispatchers.IO) {
        // ImageBitmap을 Bitmap으로 변환
        val originalBitmap = originalImage.asAndroidBitmap()

        // 비트맵 처리 작업 (CPU 집약적 작업)
        val resizedBitmap = withContext(Dispatchers.Default) {
            // 원본 비트맵 크기 확인
            val width = originalBitmap.width
            val height = originalBitmap.height

            // 이미지 비율 유지하면서 크기 조정
            if (width <= targetWidth && height <= targetHeight) {
                originalBitmap
            } else {
                // 가로, 세로 비율을 각각 계산
                val ratioWidth = targetWidth.toFloat() / width
                val ratioHeight = targetHeight.toFloat() / height
                val scaleFactor = minOf(ratioWidth, ratioHeight)

                val resizedWidth = (width * scaleFactor).toInt()
                val resizedHeight = (height * scaleFactor).toInt()

                Bitmap.createScaledBitmap(originalBitmap, resizedWidth, resizedHeight, true)
            }
        }

        // Bitmap을 ImageBitmap으로 변환하여 반환
        resizedBitmap.asImageBitmap()
    }


    private fun rotateBitmap(bitmap: Bitmap, degrees: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degrees)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }
}