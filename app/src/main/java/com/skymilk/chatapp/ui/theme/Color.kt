package com.skymilk.chatapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Black = Color(0xFF1C1E21) //Dark Background
val Blue = Color(0xFF1877F2) //Primary

val DarkRed = Color(0xFFC30052) //Dark Error
val LightRed = Color(0xFFFF84B7)

val LightBlack = Color(0xFF3A3B3C) //Dark Surface


val BlueGray = Color(0xFFA0A3BD)
val WhiteGray = Color(0xFFB0B3B8)
val LightBlueWhite = Color(0xFFF1F5F9) //Social media background

val ColorScheme.focusedTextFieldText
    @Composable
    get() = if (isSystemInDarkTheme()) Color.White else Color.Black

val ColorScheme.unfocusedTextFieldText
    @Composable
    get() = if (isSystemInDarkTheme()) Color(0xFF94A3B8) else Color(0xFF475569)

val ColorScheme.textFieldContainer
    @Composable
    get() = if (isSystemInDarkTheme()) BlueGray.copy(alpha = 0.6f) else LightBlueWhite