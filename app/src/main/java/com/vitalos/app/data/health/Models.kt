package com.vitalos.app.data.health

import java.time.Instant
import java.time.LocalDate

data class DailyMetrics(
    val date: LocalDate = LocalDate.now(),

    // Recovery / readiness
    val recoveryScore: Int = 0,           // 0-100
    val hrv: Double = 0.0,                // ms rMSSD
    val restingHeartRate: Int = 0,        // bpm
    val skinTemperature: Double = 0.0,    // °C

    // Strain
    val strainScore: Double = 0.0,        // 0-21 Whoop-style
    val activeCalories: Int = 0,          // kcal
    val totalCalories: Int = 0,           // kcal
    val steps: Int = 0,
    val distance: Double = 0.0,           // km

    // Sleep
    val sleepScore: Int = 0,              // 0-100
    val totalSleepMinutes: Int = 0,
    val deepSleepMinutes: Int = 0,
    val remSleepMinutes: Int = 0,
    val lightSleepMinutes: Int = 0,
    val awakeMinutes: Int = 0,
    val sleepEfficiency: Double = 0.0,    // %

    // Vitals
    val spo2: Double = 0.0,               // %
    val respiratoryRate: Double = 0.0,    // breaths/min

    // Data sources that contributed
    val sources: Set<String> = emptySet()
)

data class WorkoutSession(
    val id: String,
    val title: String,
    val type: WorkoutType,
    val startTime: Instant,
    val endTime: Instant,
    val durationMinutes: Int,
    val calories: Int,
    val avgHeartRate: Int,
    val maxHeartRate: Int,
    val distance: Double = 0.0,     // km, 0 if not applicable
    val source: String
)

enum class WorkoutType {
    RUNNING, STRENGTH, CYCLING, WALKING, HIIT, SWIMMING, OTHER;

    fun displayName() = when (this) {
        RUNNING  -> "Running"
        STRENGTH -> "Strength"
        CYCLING  -> "Cycling"
        WALKING  -> "Walking"
        HIIT     -> "HIIT"
        SWIMMING -> "Swimming"
        OTHER    -> "Workout"
    }

    fun emoji() = when (this) {
        RUNNING  -> "🏃"
        STRENGTH -> "🏋️"
        CYCLING  -> "🚴"
        WALKING  -> "🚶"
        HIIT     -> "⚡"
        SWIMMING -> "🏊"
        OTHER    -> "💪"
    }
}

data class HrvDataPoint(
    val date: LocalDate,
    val value: Double
)

data class HealthConnectStatus(
    val isAvailable: Boolean,
    val hasPermissions: Boolean,
    val connectedSources: List<String>
)

// Computed recovery score (0-100) from HRV baseline
fun computeRecoveryScore(
    currentHrv: Double,
    baselineHrv: Double,
    restingHr: Int,
    baselineHr: Int
): Int {
    if (baselineHrv <= 0) return 0
    val hrvContribution  = ((currentHrv / baselineHrv) * 60).coerceIn(0.0, 60.0)
    val hrContribution   = if (baselineHr > 0) {
        ((baselineHr.toDouble() / restingHr) * 40).coerceIn(0.0, 40.0)
    } else 20.0
    return (hrvContribution + hrContribution).toInt().coerceIn(0, 100)
}

// Compute Whoop-style strain (0-21) from heart rate zones
fun computeStrainScore(
    zone1Minutes: Int, zone2Minutes: Int, zone3Minutes: Int,
    zone4Minutes: Int, zone5Minutes: Int
): Double {
    val raw = (zone1Minutes * 0.5) +
              (zone2Minutes * 1.0) +
              (zone3Minutes * 2.5) +
              (zone4Minutes * 4.0) +
              (zone5Minutes * 6.0)
    return (raw / 30.0).coerceIn(0.0, 21.0)
}
