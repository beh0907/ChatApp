package com.skymilk.chatapp.store.presentation.common

import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Shader
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asImageBitmap
import coil.size.Size
import coil.transform.Transformation
import kotlin.math.min

fun Modifier.squircleClip(cornerRadius: Float = 0.8f): Modifier = this.clip(
    GenericShape { size, _ ->
        val squirclePath = Path().apply {
            val radius = cornerRadius * min(size.width, size.height)

            moveTo(0f, radius)
            cubicTo(0f, 0f, 0f, 0f, radius, 0f)
            lineTo(size.width - radius, 0f)
            cubicTo(size.width, 0f, size.width, 0f, size.width, radius)
            lineTo(size.width, size.height - radius)
            cubicTo(size.width, size.height, size.width, size.height, size.width - radius, size.height)
            lineTo(radius, size.height)
            cubicTo(0f, size.height, 0f, size.height, 0f, size.height - radius)
            close()
        }

        addPath(squirclePath)
    }
)

class SquircleTransformation(
    private val cornerRadius: Float = 0.8f,
    override val cacheKey: String = "squircle_transformation"
) : Transformation {

    override suspend fun transform(input: Bitmap, size: Size): Bitmap {
        val output = Bitmap.createBitmap(
            input.width,
            input.height,
            Bitmap.Config.ARGB_8888
        )

        val canvas = Canvas(output.asImageBitmap())
        val paint = Paint().apply {
            isAntiAlias = true
            shader = BitmapShader(input, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        }

        val path = Path().apply {
            val width = input.width.toFloat()
            val height = input.height.toFloat()
            val radius = cornerRadius * min(width, height)

            // Compose의 squircleClip과 동일한 패스 사용
            moveTo(0f, radius)
            cubicTo(0f, 0f, 0f, 0f, radius, 0f)
            lineTo(width - radius, 0f)
            cubicTo(width, 0f, width, 0f, width, radius)
            lineTo(width, height - radius)
            cubicTo(width, height, width, height, width - radius, height)
            lineTo(radius, height)
            cubicTo(0f, height, 0f, height, 0f, height - radius)
            close()
        }

        canvas.drawPath(path, paint)
        input.recycle()

        return output
    }
}