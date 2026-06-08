package com.vitalos.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vitalos.app.ui.components.*
import com.vitalos.app.ui.theme.*
import com.vitalos.app.viewmodel.DashboardUiState

// ─────────────────────────────────────────────────────────────────────────────
// SLEEP SCREEN
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun SleepScreen(uiState: DashboardUiState) {
    val m = uiState.metrics
    val totalInBed = m.totalSleepMinutes + m.awakeMinutes

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPrimary),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Sleep", style = MaterialTheme.typography.headlineLarge, color = TextPrimary)
            Spacer(Modifier.height(4.dp))
            Text("Last night's recovery", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
        }

        // Big sleep score
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(BackgroundSurface)
                    .border(1.dp, BorderSubtle, RoundedCornerShape(20.dp))
                    .padding(24.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text       = "${m.sleepScore}",
                            fontSize   = 56.sp,
                            fontWeight = FontWeight.W700,
                            color      = SleepPurple
                        )
                        Text(
                            text     = "/ 100",
                            fontSize = 20.sp,
                            color    = TextSecondary,
                            modifier = Modifier.padding(bottom = 10.dp)
                        )
                    }
                    Text(
                        text  = sleepQualityLabel(m.sleepScore),
                        style = MaterialTheme.typography.bodyMedium,
                        color = SleepPurple
                    )
                    Spacer(Modifier.height(16.dp))
                    // Stats row
                    Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                        SleepStat("Total", formatMins(m.totalSleepMinutes))
                        SleepStat("In Bed", formatMins(totalInBed))
                        SleepStat("Efficiency", "${m.sleepEfficiency.toInt()}%")
                    }
                }
            }
        }

        // Stage breakdown
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(BackgroundSurface)
                    .border(1.dp, BorderSubtle, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Sleep Stages", fontWeight = FontWeight.W600, fontSize = 13.sp, color = TextPrimary)
                    Spacer(Modifier.height(4.dp))
                    SleepStageBar("Awake",  m.awakeMinutes,      totalInBed, Color(0xFF888888))
                    SleepStageBar("Light",  m.lightSleepMinutes, totalInBed, SleepPurple.copy(alpha = 0.5f))
                    SleepStageBar("Deep",   m.deepSleepMinutes,  totalInBed, SleepPurple)
                    SleepStageBar("REM",    m.remSleepMinutes,   totalInBed, AccentBlue)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text  = "Source: Porodo Band via Health Connect",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                        color = TextTertiary
                    )
                }
            }
        }
    }
}

@Composable
private fun SleepStat(label: String, value: String) {
    Column {
        Text(text = value, fontWeight = FontWeight.W700, fontSize = 16.sp, color = TextPrimary)
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
    }
}

private fun sleepQualityLabel(score: Int) = when {
    score >= 85 -> "Excellent recovery"
    score >= 70 -> "Good recovery"
    score >= 55 -> "Okay recovery"
    else        -> "Poor recovery"
}

private fun formatMins(minutes: Int): String {
    if (minutes == 0) return "--"
    val h = minutes / 60; val m = minutes % 60
    return "${h}h ${m}m"
}

// ─────────────────────────────────────────────────────────────────────────────
// HEALTH SCREEN
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun HealthScreen(uiState: DashboardUiState) {
    val m = uiState.metrics
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPrimary),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Health", style = MaterialTheme.typography.headlineLarge, color = TextPrimary)
            Text("Vitals & biometrics", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                VitalCard(Modifier.weight(1f), "❤️", "Heart Rate", "${m.restingHeartRate}", "bpm resting", StrainRed)
                VitalCard(Modifier.weight(1f), "⚡", "HRV", "${m.hrv.toInt()}", "ms rMSSD", RecoveryGreen)
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                VitalCard(Modifier.weight(1f), "💧", "SpO2", "${m.spo2.toInt()}", "% oxygen", AccentBlue)
                VitalCard(Modifier.weight(1f), "🌡️", "Skin Temp", String.format("%.1f", m.skinTemperature), "°C", AccentYellow)
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                VitalCard(Modifier.weight(1f), "🫁", "Resp. Rate", String.format("%.1f", m.respiratoryRate), "breaths/min", AccentOrange)
                VitalCard(Modifier.weight(1f), "🔥", "Active Cal", "${m.activeCalories}", "kcal burned", AccentOrange)
            }
        }
    }
}

@Composable
private fun VitalCard(
    modifier: Modifier, emoji: String, name: String,
    value: String, sub: String, color: Color
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(BackgroundSurface)
            .border(1.dp, BorderSubtle, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Text(emoji, fontSize = 24.sp)
        Spacer(Modifier.height(10.dp))
        Text(text = name, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
        Spacer(Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.Baseline, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(text = value, fontSize = 28.sp, fontWeight = FontWeight.W700, color = color)
        }
        Text(text = sub, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// ACTIVITY SCREEN
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun ActivityScreen(uiState: DashboardUiState) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPrimary),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text("Activity", style = MaterialTheme.typography.headlineLarge, color = TextPrimary)
            Text("Recent workouts", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
        }

        if (uiState.recentWorkouts.isEmpty()) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No recent workouts found", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                }
            }
        } else {
            items(uiState.recentWorkouts.size) { idx ->
                val w = uiState.recentWorkouts[idx]
                WorkoutCard(
                    emoji     = w.type.emoji(),
                    name      = w.title,
                    meta      = w.source,
                    statValue = "${w.durationMinutes} min",
                    statLabel = "duration"
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// TRENDS SCREEN
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun TrendsScreen(uiState: DashboardUiState) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPrimary),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Trends", style = MaterialTheme.typography.headlineLarge, color = TextPrimary)
            Text("7-day overview", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
        }

        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(BackgroundSurface)
                    .border(1.dp, BorderSubtle, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text       = "${uiState.metrics.hrv.toInt()}",
                            fontSize   = 36.sp,
                            fontWeight = FontWeight.W700,
                            color      = TextPrimary
                        )
                        Text("ms avg HRV", style = MaterialTheme.typography.bodyMedium, color = TextSecondary, modifier = Modifier.padding(bottom = 4.dp))
                    }
                    Spacer(Modifier.height(12.dp))
                    // Mini bar chart for HRV trend
                    if (uiState.hrvTrend.isNotEmpty()) {
                        val max = uiState.hrvTrend.maxOfOrNull { it.value } ?: 100.0
                        Row(
                            modifier = Modifier.fillMaxWidth().height(60.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.Bottom
                        ) {
                            uiState.hrvTrend.forEach { point ->
                                val frac = (point.value / max).toFloat().coerceIn(0.1f, 1f)
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight(frac)
                                        .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                        .background(RecoveryGreen.copy(alpha = 0.7f))
                                )
                            }
                        }
                        Spacer(Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            val days = listOf("M","T","W","T","F","S","S")
                            uiState.hrvTrend.forEachIndexed { idx, _ ->
                                Text(
                                    text     = days.getOrElse(idx) { "" },
                                    modifier = Modifier.weight(1f),
                                    style    = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                    color    = TextSecondary
                                )
                            }
                        }
                    } else {
                        Text("Collecting data...", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// SETUP / PERMISSION SCREENS
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun HealthConnectSetupScreen(onRequestPermissions: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPrimary)
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("⚡", fontSize = 56.sp)
        Spacer(Modifier.height(24.dp))
        Text("VitalOS", style = MaterialTheme.typography.headlineLarge, color = TextPrimary)
        Spacer(Modifier.height(8.dp))
        Text(
            text  = "Health Connect is not installed. Install it from the Play Store to continue.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )
        Spacer(Modifier.height(32.dp))
        Button(
            onClick = onRequestPermissions,
            colors  = ButtonDefaults.buttonColors(containerColor = RecoveryGreen)
        ) {
            Text("Install Health Connect", color = Color.Black, fontWeight = FontWeight.W600)
        }
    }
}

@Composable
fun PermissionsScreen(onRequestPermissions: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPrimary)
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("🔐", fontSize = 56.sp)
        Spacer(Modifier.height(24.dp))
        Text("Permissions Required", style = MaterialTheme.typography.headlineLarge, color = TextPrimary)
        Spacer(Modifier.height(8.dp))
        Text(
            text  = "VitalOS needs access to your Health Connect data to show your recovery, sleep, and activity metrics.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )
        Spacer(Modifier.height(32.dp))
        Button(
            onClick = onRequestPermissions,
            colors  = ButtonDefaults.buttonColors(containerColor = RecoveryGreen)
        ) {
            Text("Grant Permissions", color = Color.Black, fontWeight = FontWeight.W600)
        }
    }
}
