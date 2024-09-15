package com.skymilk.chatapp.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.skymilk.chatapp.R

val HannaPro = FontFamily(
    listOf(
        Font(resId = R.font.bm_hanna_pro, weight = FontWeight.Medium),
        Font(resId = R.font.bm_hanna_pro, weight = FontWeight.Normal),
        Font(resId = R.font.bm_hanna_pro, weight = FontWeight.ExtraBold),
    )
)

// Set of Material typography styles to start with
val CompactTypography = Typography(
    headlineLarge = TextStyle(
        fontFamily = HannaPro,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 32.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = HannaPro,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp
    ),
    titleMedium = TextStyle(
        fontFamily = HannaPro,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp
    ),
    labelMedium = TextStyle(
        fontFamily = HannaPro,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
    )
)

val CompactMediumTypography = Typography(
    headlineLarge = TextStyle(
        fontFamily = HannaPro,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 28.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = HannaPro,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp
    ),
    titleMedium = TextStyle(
        fontFamily = HannaPro,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp
    ),
    labelMedium = TextStyle(
        fontFamily = HannaPro,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
    )
)

val CompactSmallTypography = Typography(
    headlineLarge = TextStyle(
        fontFamily = HannaPro,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 22.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = HannaPro,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp
    ),
    titleMedium = TextStyle(
        fontFamily = HannaPro,
        fontWeight = FontWeight.Medium,
        fontSize = 10.sp
    ),
    labelMedium = TextStyle(
        fontFamily = HannaPro,
        fontWeight = FontWeight.Normal,
        fontSize = 10.sp
    )
)

val MediumTypography = Typography(
    headlineLarge = TextStyle(
        fontFamily = HannaPro,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 38.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = HannaPro,
        fontWeight = FontWeight.Bold,
        fontSize = 30.sp
    ),
    titleMedium = TextStyle(
        fontFamily = HannaPro,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp
    ),
    labelMedium = TextStyle(
        fontFamily = HannaPro,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    )
)

val ExpandedTypography = Typography(
    headlineLarge = TextStyle(
        fontFamily = HannaPro,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 42.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = HannaPro,
        fontWeight = FontWeight.Bold,
        fontSize = 34.sp
    ),
    titleMedium = TextStyle(
        fontFamily = HannaPro,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp
    ),
    labelMedium = TextStyle(
        fontFamily = HannaPro,
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp
    )
)