package com.skymilk.chatapp.store.presentation.common

import androidx.compose.foundation.shape.GenericShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Path
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