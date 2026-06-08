package com.vitalos.app.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vitalos.app.data.health.WorkoutType
import com.vitalos.app.ui.components.*
import com.vitalos.app.ui.theme.*
import com.vitalos.app.viewmodel.DashboardUiState
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodayScreen(uiState: DashboardUiState, onRefresh: () -> Unit) {
    val dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, d MMM yyyy"))
        .uppercase()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPrimary),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // Top bar
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text  = dateStr,
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                    Text(
                        text  = "Good morning, Hamza",
                        style = MaterialTheme.typography.headlineMedium,
                        color = TextPrimary
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = RecoveryGreen
                        )
                    } else {
                        IconButton(onClick = onRefresh, modifier = Modifier.size(36.dp)) {
                            Icon(Icons.Filled.Refresh, contentDescription = "Refresh", tint = TextSecondary, modifier = Modifier.size(18.dp))
                        }
                    }
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(StrainRed, AccentOrange))),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("H", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }
        }

        // Recovery / Strain / Sleep score cards
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ScoreCard(
                    modifier = Modifier.weight(1f),
                    label    = "Recovery",
                    value    = "${uiState.metrics.recoveryScore}%",
                    sub      = "HRV ${uiState.metrics.hrv.toInt()}ms",
                    color    = RecoveryGreen
                )
                ScoreCard(
                    modifier = Modifier.weight(1f),
                    label    = "Strain",
                    value    = String.format("%.1f", uiState.metrics.strainScore),
                    sub      = strainLabel(uiState.metrics.strainScore),
                    color    = StrainRed
                )
                ScoreCard(
                    modifier = Modifier.weight(1f),
                    label    = "Sleep",
                    value    = "${uiState.metrics.sleepScore}%",
                    sub      = formatSleepTime(uiState.metrics.totalSleepMinutes),
                    color    = SleepPurple
                )
            }
        }

        item { Spacer(Modifier.height(20.dp)) }

        // Today's metrics grid
        item {
            SectionHeader(title = "Today's Metrics", modifier = Modifier.padding(horizontal = 20.dp))
            Spacer(Modifier.height(12.dp))
        }

        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    MetricCard(
                        modifier    = Modifier.weight(1f),
                        emoji       = "❤️",
                        emojiColor  = StrainRed,
                        name        = "Resting HR",
                        value       = "${uiState.metrics.restingHeartRate}",
                        unit        = "bpm",
                        source      = "Samsung Health",
                        progress    = (uiState.metrics.restingHeartRate / 100f).coerceIn(0f, 1f),
                        barColor    = StrainRed
                    )
                    MetricCard(
                        modifier    = Modifier.weight(1f),
                        emoji       = "⚡",
                        emojiColor  = RecoveryGreen,
                        name        = "HRV",
                        value       = "${uiState.metrics.hrv.toInt()}",
                        unit        = "ms",
                        source      = "Galaxy Watch 3",
                        progress    = (uiState.metrics.hrv / 100f).coerceIn(0f, 1f).toFloat(),
                        barColor    = RecoveryGreen
                    )
                }
                Spacer(Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    MetricCard(
                        modifier    = Modifier.weight(1f),
                        emoji       = "🔥",
                        emojiColor  = AccentOrange,
                        name        = "Calories",
                        value       = "${uiState.metrics.totalCalories}",
                        unit        = "kcal",
                        source      = "Samsung Health",
                        progress    = (uiState.metrics.totalCalories / 2500f).coerceIn(0f, 1f),
                        barColor    = AccentOrange
                    )
                    MetricCard(
                        modifier    = Modifier.weight(1f),
                        emoji       = "🦶",
                        emojiColor  = SleepPurple,
                        name        = "Steps",
                        value       = formatSteps(uiState.metrics.steps),
                        unit        = "",
                        source      = "Porodo Band",
                        progress    = (uiState.metrics.steps / 10000f).coerceIn(0f, 1f),
                        barColor    = SleepPurple
                    )
                }
                Spacer(Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    MetricCard(
                        modifier    = Modifier.weight(1f),
                        emoji       = "💧",
                        emojiColor  = AccentBlue,
                        name        = "SpO2",
                        value       = "${uiState.metrics.spo2.toInt()}",
                        unit        = "%",
                        source      = "Galaxy Watch 3",
                        progress    = (uiState.metrics.spo2 / 100f).coerceIn(0f, 1f).toFloat(),
                        barColor    = AccentBlue
                    )
                    MetricCard(
                        modifier    = Modifier.weight(1f),
                        emoji       = "🌡️",
                        emojiColor  = AccentYellow,
                        name        = "Skin Temp",
                        value       = String.format("%.1f", uiState.metrics.skinTemperature),
                        unit        = "°C",
                        source      = "Porodo Band",
                        progress    = ((uiState.metrics.skinTemperature - 35.0) / 4.0).coerceIn(0.0, 1.0).toFloat(),
                        barColor    = AccentYellow
                    )
                }
            }
        }

        item { Spacer(Modifier.height(20.dp)) }

        // Recent workouts
        if (uiState.recentWorkouts.isNotEmpty()) {
            item {
                SectionHeader(title = "Recent Activities", modifier = Modifier.padding(horizontal = 20.dp))
                Spacer(Modifier.height(12.dp))
            }
            items(minOf(uiState.recentWorkouts.size, 3)) { idx ->
                val workout = uiState.recentWorkouts[idx]
                WorkoutCard(
                    modifier  = Modifier.padding(horizontal = 20.dp, vertical = 4.dp),
                    emoji     = workout.type.emoji(),
                    name      = workout.title,
                    meta      = "${workout.source} · ${formatTime(workout.startTime)}",
                    statValue = if (workout.type == WorkoutType.RUNNING && workout.distance > 0)
                        String.format("%.1f km", workout.distance)
                    else
                        "${workout.durationMinutes} min",
                    statLabel = if (workout.type == WorkoutType.RUNNING && workout.distance > 0)
                        formatDuration(workout.durationMinutes)
                    else
                        "duration"
                )
            }
        }

        item { Spacer(Modifier.height(20.dp)) }

        // Connected sources
        item {
            SectionHeader(title = "Connected Sources", modifier = Modifier.padding(horizontal = 20.dp))
            Spacer(Modifier.height(10.dp))
            SourceChips(
                sources  = uiState.healthStatus.connectedSources,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text     = "All data aggregated via Health Connect",
                style    = MaterialTheme.typography.labelSmall,
                color    = TextTertiary,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
        }
    }
}

private fun strainLabel(score: Double) = when {
    score < 4  -> "Light effort"
    score < 9  -> "Moderate"
    score < 14 -> "High effort"
    score < 18 -> "Very high"
    else       -> "All out"
}

private fun formatSleepTime(minutes: Int): String {
    if (minutes == 0) return "--"
    val h = minutes / 60; val m = minutes % 60
    return "${h}h ${m}m"
}

private fun formatSteps(steps: Int): String {
    return if (steps >= 1000) String.format("%.1fk", steps / 1000f) else "$steps"
}

private fun formatDuration(minutes: Int): String {
    val h = minutes / 60; val m = minutes % 60
    return if (h > 0) "${h}h ${m}m" else "${m}m"
}

private fun formatTime(instant: java.time.Instant): String {
    val zdt = instant.atZone(java.time.ZoneId.systemDefault())
    return String.format("%d:%02d %s",
        if (zdt.hour % 12 == 0) 12 else zdt.hour % 12,
        zdt.minute,
        if (zdt.hour < 12) "AM" else "PM"
    )
}
