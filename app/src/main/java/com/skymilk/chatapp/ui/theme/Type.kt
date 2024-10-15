package com.skymilk.chatapp.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.skymilk.chatapp.R

val SamsungOneFont = FontFamily(
    listOf(
        Font(resId = R.font.samsungone_arabic_300, weight = FontWeight.Thin),
        Font(resId = R.font.samsungone_arabic_400, weight = FontWeight.Light),
        Font(resId = R.font.samsungone_arabic_450, weight = FontWeight.Normal),
        Font(resId = R.font.samsungone_arabic_600, weight = FontWeight.SemiBold),
        Font(resId = R.font.samsungone_arabic_700, weight = FontWeight.Bold),
    )
)

// CompactTypography
val CompactTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = SamsungOneFont,
        fontWeight = FontWeight.Light,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp
    ),
    displayMedium = TextStyle(
        fontFamily = SamsungOneFont,
        fontWeight = FontWeight.Light,
        fontSize = 45.sp,
        lineHeight = 52.sp,
        letterSpacing = 0.sp
    ),
    displaySmall = TextStyle(
        fontFamily = SamsungOneFont,
        fontWeight = FontWeight.Normal,
        fontSize = 36.sp,
        lineHeight = 44.sp,
        letterSpacing = 0.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = SamsungOneFont,
        fontWeight = FontWeight.SemiBold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = SamsungOneFont,
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = SamsungOneFont,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    ),
    titleLarge = TextStyle(
        fontFamily = SamsungOneFont,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontFamily = SamsungOneFont,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    ),
    titleSmall = TextStyle(
        fontFamily = SamsungOneFont,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = SamsungOneFont,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = SamsungOneFont,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    bodySmall = TextStyle(
        fontFamily = SamsungOneFont,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    ),
    labelLarge = TextStyle(
        fontFamily = SamsungOneFont,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontFamily = SamsungOneFont,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontFamily = SamsungOneFont,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)

// CompactMediumTypography (약간 더 작은 크기)
val CompactMediumTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = SamsungOneFont,
        fontWeight = FontWeight.Light,
        fontSize = 52.sp,
        lineHeight = 58.sp,
        letterSpacing = (-0.25).sp
    ),
    displayMedium = TextStyle(
        fontFamily = SamsungOneFont,
        fontWeight = FontWeight.Light,
        fontSize = 41.sp,
        lineHeight = 48.sp,
        letterSpacing = 0.sp
    ),
    displaySmall = TextStyle(
        fontFamily = SamsungOneFont,
        fontWeight = FontWeight.Normal,
        fontSize = 33.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = SamsungOneFont,
        fontWeight = FontWeight.SemiBold,
        fontSize = 29.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = SamsungOneFont,
        fontWeight = FontWeight.SemiBold,
        fontSize = 25.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = SamsungOneFont,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    titleLarge = TextStyle(
        fontFamily = SamsungOneFont,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 26.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontFamily = SamsungOneFont,
        fontWeight = FontWeight.SemiBold,
        fontSize = 15.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.15.sp
    ),
    titleSmall = TextStyle(
        fontFamily = SamsungOneFont,
        fontWeight = FontWeight.Bold,
        fontSize = 13.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.1.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = SamsungOneFont,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = SamsungOneFont,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.25.sp
    ),
    bodySmall = TextStyle(
        fontFamily = SamsungOneFont,
        fontWeight = FontWeight.Normal,
        fontSize = 11.sp,
        lineHeight = 14.sp,
        letterSpacing = 0.4.sp
    ),
    labelLarge = TextStyle(
        fontFamily = SamsungOneFont,
        fontWeight = FontWeight.Medium,
        fontSize = 13.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontFamily = SamsungOneFont,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 14.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontFamily = SamsungOneFont,
        fontWeight = FontWeight.Medium,
        fontSize = 10.sp,
        lineHeight = 14.sp,
        letterSpacing = 0.5.sp
    )
)

// CompactSmallTypography (더 작은 크기)
val CompactSmallTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = SamsungOneFont,
        fontWeight = FontWeight.Light,
        fontSize = 47.sp,
        lineHeight = 52.sp,
        letterSpacing = (-0.25).sp
    ),
    displayMedium = TextStyle(
        fontFamily = SamsungOneFont,
        fontWeight = FontWeight.Light,
        fontSize = 37.sp,
        lineHeight = 44.sp,
        letterSpacing = 0.sp
    ),
    displaySmall = TextStyle(
        fontFamily = SamsungOneFont,
        fontWeight = FontWeight.Normal,
        fontSize = 30.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = SamsungOneFont,
        fontWeight = FontWeight.SemiBold,
        fontSize = 26.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = SamsungOneFont,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = SamsungOneFont,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp
    ),
    titleLarge = TextStyle(
        fontFamily = SamsungOneFont,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontFamily = SamsungOneFont,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.15.sp
    ),
    titleSmall = TextStyle(
        fontFamily = SamsungOneFont,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.1.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = SamsungOneFont,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = SamsungOneFont,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.25.sp
    ),
    bodySmall = TextStyle(
        fontFamily = SamsungOneFont,
        fontWeight = FontWeight.Normal,
        fontSize = 10.sp,
        lineHeight = 12.sp,
        letterSpacing = 0.4.sp
    ),
    labelLarge = TextStyle(
        fontFamily = SamsungOneFont,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontFamily = SamsungOneFont,
        fontWeight = FontWeight.Medium,
        fontSize = 10.sp,
        lineHeight = 12.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontFamily = SamsungOneFont,
        fontWeight = FontWeight.Medium,
        fontSize = 9.sp,
        lineHeight = 12.sp,
        letterSpacing = 0.5.sp
    )
)

// MediumTypography
val MediumTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = SamsungOneFont,
        fontWeight = FontWeight.Light,
        fontSize = 62.sp,
        lineHeight = 70.sp,
        letterSpacing = (-0.25).sp
    ),
    displayMedium = TextStyle(
        fontFamily = SamsungOneFont,
        fontWeight = FontWeight.Light,
        fontSize = 50.sp,
        lineHeight = 58.sp,
        letterSpacing = 0.sp
    ),
    displaySmall = TextStyle(
        fontFamily = SamsungOneFont,
        fontWeight = FontWeight.Normal,
        fontSize = 40.sp,
        lineHeight = 48.sp,
        letterSpacing = 0.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = SamsungOneFont,
        fontWeight = FontWeight.SemiBold,
        fontSize = 36.sp,
        lineHeight = 44.sp,
        letterSpacing = 0.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = SamsungOneFont,
        fontWeight = FontWeight.SemiBold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = SamsungOneFont,
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp
    ),
    titleLarge = TextStyle(
        fontFamily = SamsungOneFont,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontFamily = SamsungOneFont,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        lineHeight = 26.sp,
        letterSpacing = 0.15.sp
    ),
    titleSmall = TextStyle(
        fontFamily = SamsungOneFont,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.1.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = SamsungOneFont,
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp,
        lineHeight = 26.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = SamsungOneFont,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.25.sp
    ),
    bodySmall = TextStyle(
        fontFamily = SamsungOneFont,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.4.sp
    ),
    labelLarge = TextStyle(
        fontFamily = SamsungOneFont,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontFamily = SamsungOneFont,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontFamily = SamsungOneFont,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)

// ExpandedTypography
val ExpandedTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = SamsungOneFont,
        fontWeight = FontWeight.Light,
        fontSize = 68.sp,
        lineHeight = 76.sp,
        letterSpacing = (-0.25).sp
    ),
    displayMedium = TextStyle(
        fontFamily = SamsungOneFont,
        fontWeight = FontWeight.Light,
        fontSize = 55.sp,
        lineHeight = 64.sp,
        letterSpacing = 0.sp
    ),
    displaySmall = TextStyle(
        fontFamily = SamsungOneFont,
        fontWeight = FontWeight.Normal,
        fontSize = 44.sp,
        lineHeight = 52.sp,
        letterSpacing = 0.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = SamsungOneFont,
        fontWeight = FontWeight.SemiBold,
        fontSize = 40.sp,
        lineHeight = 48.sp,
        letterSpacing = 0.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = SamsungOneFont,
        fontWeight = FontWeight.SemiBold,
        fontSize = 36.sp,
        lineHeight = 44.sp,
        letterSpacing = 0.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = SamsungOneFont,
        fontWeight = FontWeight.SemiBold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp
    ),
    titleLarge = TextStyle(
        fontFamily = SamsungOneFont,
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontFamily = SamsungOneFont,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.15.sp
    ),
    titleSmall = TextStyle(
        fontFamily = SamsungOneFont,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.1.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = SamsungOneFont,
        fontWeight = FontWeight.Normal,
        fontSize = 20.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = SamsungOneFont,
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.25.sp
    ),
    bodySmall = TextStyle(
        fontFamily = SamsungOneFont,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.4.sp
    ),
    labelLarge = TextStyle(
        fontFamily = SamsungOneFont,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontFamily = SamsungOneFont,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontFamily = SamsungOneFont,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.5.sp
    )
)