package com.vitalos.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val Typography = Typography(
    displayLarge = TextStyle(
        fontWeight = FontWeight.W700,
        fontSize   = 48.sp,
        lineHeight = 52.sp,
        color      = TextPrimary
    ),
    displayMedium = TextStyle(
        fontWeight = FontWeight.W700,
        fontSize   = 36.sp,
        lineHeight = 40.sp,
        color      = TextPrimary
    ),
    headlineLarge = TextStyle(
        fontWeight = FontWeight.W600,
        fontSize   = 24.sp,
        lineHeight = 28.sp,
        color      = TextPrimary
    ),
    headlineMedium = TextStyle(
        fontWeight = FontWeight.W600,
        fontSize   = 20.sp,
        lineHeight = 24.sp,
        color      = TextPrimary
    ),
    titleLarge = TextStyle(
        fontWeight = FontWeight.W600,
        fontSize   = 16.sp,
        lineHeight = 20.sp,
        color      = TextPrimary
    ),
    titleMedium = TextStyle(
        fontWeight = FontWeight.W500,
        fontSize   = 14.sp,
        lineHeight = 18.sp,
        color      = TextPrimary
    ),
    bodyLarge = TextStyle(
        fontWeight = FontWeight.W400,
        fontSize   = 16.sp,
        lineHeight = 24.sp,
        color      = TextPrimary
    ),
    bodyMedium = TextStyle(
        fontWeight = FontWeight.W400,
        fontSize   = 14.sp,
        lineHeight = 20.sp,
        color      = TextSecondary
    ),
    bodySmall = TextStyle(
        fontWeight = FontWeight.W400,
        fontSize   = 12.sp,
        lineHeight = 16.sp,
        color      = TextSecondary
    ),
    labelLarge = TextStyle(
        fontWeight = FontWeight.W500,
        fontSize   = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.08.sp,
        color      = TextSecondary
    ),
    labelSmall = TextStyle(
        fontWeight = FontWeight.W500,
        fontSize   = 10.sp,
        lineHeight = 14.sp,
        letterSpacing = 0.10.sp,
        color      = TextSecondary
    )
)
