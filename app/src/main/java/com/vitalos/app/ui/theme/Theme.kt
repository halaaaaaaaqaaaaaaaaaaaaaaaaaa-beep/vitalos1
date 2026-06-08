package com.vitalos.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary          = RecoveryGreen,
    onPrimary        = Color(0xFF003319),
    primaryContainer = Color(0xFF004D26),
    secondary        = SleepPurple,
    onSecondary      = Color(0xFF1A0066),
    tertiary         = AccentBlue,
    background       = BackgroundPrimary,
    surface          = BackgroundSurface,
    surfaceVariant   = BackgroundSurface2,
    onBackground     = TextPrimary,
    onSurface        = TextPrimary,
    onSurfaceVariant = TextSecondary,
    outline          = BorderSubtle,
    error            = StrainRed,
)

@Composable
fun VitalOSTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography  = Typography,
        content     = content
    )
}
