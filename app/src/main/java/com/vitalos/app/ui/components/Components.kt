package com.vitalos.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import com.vitalos.app.ui.theme.*

@Composable
fun ScoreCard(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    sub: String,
    color: Color
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(BackgroundSurface)
            .border(1.dp, BorderSubtle, RoundedCornerShape(16.dp))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                Box(modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(color))
                Text(
                    text  = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
            }
            Spacer(Modifier.height(6.dp))
            Text(
                text       = value,
                fontSize   = 26.sp,
                fontWeight = FontWeight.W700,
                color      = color
            )
            Text(
                text  = sub,
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(3.dp)
                .clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp))
                .background(color)
                .align(Alignment.BottomCenter)
        )
    }
}

@Composable
fun MetricCard(
    modifier: Modifier = Modifier,
    emoji: String,
    emojiColor: Color,
    name: String,
    value: String,
    unit: String,
    source: String,
    progress: Float,
    barColor: Color
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(BackgroundSurface)
            .border(1.dp, BorderSubtle, RoundedCornerShape(14.dp))
            .padding(14.dp)
    ) {
        Box(
            modifier = Modifier
                .size(30.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(emojiColor.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Text(emoji, fontSize = 16.sp)
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text  = name,
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary
        )
        Spacer(Modifier.height(3.dp))
        Row(verticalAlignment = Alignment.Baseline, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text       = value,
                fontSize   = 20.sp,
                fontWeight = FontWeight.W700,
                color      = TextPrimary
            )
            if (unit.isNotEmpty()) {
                Text(
                    text  = unit,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
            }
        }
        Text(
            text  = source,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
            color = TextTertiary
        )
        Spacer(Modifier.height(8.dp))
        LinearProgressIndicator(
            progress     = { progress },
            modifier     = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp)),
            color        = barColor,
            trackColor   = BackgroundSurface2
        )
    }
}

@Composable
fun WorkoutCard(
    modifier: Modifier = Modifier,
    emoji: String,
    name: String,
    meta: String,
    statValue: String,
    statLabel: String
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(BackgroundSurface)
            .border(1.dp, BorderSubtle, RoundedCornerShape(14.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(AccentBlue.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Text(emoji, fontSize = 20.sp)
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text       = name,
                fontWeight = FontWeight.W600,
                fontSize   = 13.sp,
                color      = TextPrimary
            )
            Text(
                text  = meta,
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary
            )
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text       = statValue,
                fontSize   = 16.sp,
                fontWeight = FontWeight.W700,
                color      = AccentBlue
            )
            Text(
                text  = statLabel,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                color = TextSecondary
            )
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    action: String = "",
    actionColor: Color = TextSecondary,
    onAction: () -> Unit = {}
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text       = title,
            fontWeight = FontWeight.W600,
            fontSize   = 13.sp,
            letterSpacing = 0.5.sp,
            color      = TextPrimary
        )
        if (action.isNotEmpty()) {
            TextButton(onClick = onAction, contentPadding = PaddingValues(0.dp)) {
                Text(text = action, style = MaterialTheme.typography.labelSmall, color = actionColor)
            }
        }
    }
}

@Composable
fun SourceChips(sources: List<String>, modifier: Modifier = Modifier) {
    val colors = listOf(RecoveryGreen, StrainRed, AccentOrange, SleepPurple, AccentBlue, AccentYellow)
    LazyRow(
        modifier            = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        items(sources) { source ->
            val color = colors[sources.indexOf(source) % colors.size]
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(BackgroundSurface2)
                    .border(1.dp, BorderMedium, RoundedCornerShape(20.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Box(modifier = Modifier
                    .size(5.dp)
                    .clip(CircleShape)
                    .background(color))
                Text(
                    text  = source,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextPrimary
                )
            }
        }
    }
}

@Composable
fun SleepStageBar(
    label: String,
    minutes: Int,
    maxMinutes: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    val progress = if (maxMinutes > 0) (minutes.toFloat() / maxMinutes).coerceIn(0f, 1f) else 0f
    val h = minutes / 60; val m = minutes % 60
    val timeStr = if (h > 0) "${h}h ${m}m" else "${m}m"

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text     = label,
            style    = MaterialTheme.typography.labelSmall,
            color    = TextSecondary,
            modifier = Modifier.width(48.dp)
        )
        LinearProgressIndicator(
            progress   = { progress },
            modifier   = Modifier
                .weight(1f)
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color      = color,
            trackColor = BackgroundSurface2
        )
        Text(
            text     = timeStr,
            style    = MaterialTheme.typography.labelSmall,
            color    = TextSecondary,
            modifier = Modifier.width(40.dp)
        )
    }
}
