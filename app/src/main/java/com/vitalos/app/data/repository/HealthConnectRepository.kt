package com.vitalos.app.data.repository

import android.content.Context
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.*
import androidx.health.connect.client.request.AggregateRequest
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import com.vitalos.app.data.health.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.*
import java.time.temporal.ChronoUnit
import kotlin.math.sqrt

class HealthConnectRepository(private val context: Context) {

    private val client: HealthConnectClient by lazy {
        HealthConnectClient.getOrCreate(context)
    }

    val requiredPermissions = setOf(
        HealthPermission.getReadPermission(HeartRateRecord::class),
        HealthPermission.getReadPermission(RestingHeartRateRecord::class),
        HealthPermission.getReadPermission(StepsRecord::class),
        HealthPermission.getReadPermission(SleepSessionRecord::class),
        HealthPermission.getReadPermission(ExerciseSessionRecord::class),
        HealthPermission.getReadPermission(TotalCaloriesBurnedRecord::class),
        HealthPermission.getReadPermission(ActiveCaloriesBurnedRecord::class),
        HealthPermission.getReadPermission(DistanceRecord::class),
        HealthPermission.getReadPermission(HeartRateVariabilityRmssdRecord::class),
        HealthPermission.getReadPermission(OxygenSaturationRecord::class),
        HealthPermission.getReadPermission(BodyTemperatureRecord::class),
        HealthPermission.getReadPermission(RespiratoryRateRecord::class),
    )

    suspend fun checkAvailability(): HealthConnectStatus {
        val sdkStatus = HealthConnectClient.getSdkStatus(context)
        val isAvailable = sdkStatus == HealthConnectClient.SDK_AVAILABLE
        if (!isAvailable) return HealthConnectStatus(false, false, emptyList())

        val granted = client.permissionController.getGrantedPermissions()
        val hasAll  = granted.containsAll(requiredPermissions)
        val sources = getConnectedSources()
        return HealthConnectStatus(true, hasAll, sources)
    }

    private suspend fun getConnectedSources(): List<String> {
        return try {
            val end   = Instant.now()
            val start = end.minus(7, ChronoUnit.DAYS)
            val range = TimeRangeFilter.between(start, end)
            val stepRecords = client.readRecords(
                ReadRecordsRequest(StepsRecord::class, timeRangeFilter = range)
            ).records
            stepRecords.mapNotNull { it.metadata.dataOrigin.packageName }
                .distinct()
                .map { packageToFriendlyName(it) }
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun packageToFriendlyName(pkg: String) = when {
        pkg.contains("samsung.android.shealth") -> "Samsung Health"
        pkg.contains("nike")                    -> "Nike Run Club"
        pkg.contains("strong")                  -> "Strong"
        pkg.contains("porodo")                  -> "Porodo"
        pkg.contains("fitbit")                  -> "Fitbit"
        pkg.contains("garmin")                  -> "Garmin Connect"
        pkg.contains("strava")                  -> "Strava"
        pkg.contains("google.android.apps.fitness") -> "Google Fit"
        else -> pkg.substringAfterLast(".").replaceFirstChar { it.uppercase() }
    }

    suspend fun getTodayMetrics(): DailyMetrics {
        val today     = LocalDate.now()
        val startOfDay = today.atStartOfDay(ZoneId.systemDefault()).toInstant()
        val endOfDay   = today.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()
        val range      = TimeRangeFilter.between(startOfDay, endOfDay)

        val hrv            = readLatestHrv(range)
        val restingHr      = readRestingHeartRate(range)
        val steps          = readSteps(range)
        val activeCalories = readActiveCalories(range)
        val totalCalories  = readTotalCalories(range)
        val distance       = readDistance(range)
        val spo2           = readLatestSpo2(range)
        val skinTemp       = readLatestBodyTemp(range)
        val respiratoryRate = readLatestRespiratoryRate(range)
        val sleep          = readLastNightSleep()
        val sources        = getConnectedSources().toSet()

        // Compute recovery from 7-day HRV baseline
        val baselineHrv = getHrvBaseline()
        val baselineHr  = getRestingHrBaseline()
        val recoveryScore = if (hrv > 0 && baselineHrv > 0)
            computeRecoveryScore(hrv, baselineHrv, restingHr, baselineHr)
        else 0

        // Compute strain from heart rate zones today
        val strain = computeStrainFromExercise(range)

        return DailyMetrics(
            date               = today,
            recoveryScore      = recoveryScore,
            hrv                = hrv,
            restingHeartRate   = restingHr,
            skinTemperature    = skinTemp,
            strainScore        = strain,
            activeCalories     = activeCalories,
            totalCalories      = totalCalories,
            steps              = steps,
            distance           = distance,
            sleepScore         = sleep.sleepScore,
            totalSleepMinutes  = sleep.totalSleepMinutes,
            deepSleepMinutes   = sleep.deepSleepMinutes,
            remSleepMinutes    = sleep.remSleepMinutes,
            lightSleepMinutes  = sleep.lightSleepMinutes,
            awakeMinutes       = sleep.awakeMinutes,
            sleepEfficiency    = sleep.sleepEfficiency,
            spo2               = spo2,
            respiratoryRate    = respiratoryRate,
            sources            = sources
        )
    }

    private suspend fun readLatestHrv(range: TimeRangeFilter): Double {
        return try {
            val records = client.readRecords(
                ReadRecordsRequest(HeartRateVariabilityRmssdRecord::class, range)
            ).records
            records.lastOrNull()?.heartRateVariabilityMillis ?: 0.0
        } catch (e: Exception) { 0.0 }
    }

    private suspend fun readRestingHeartRate(range: TimeRangeFilter): Int {
        return try {
            val records = client.readRecords(
                ReadRecordsRequest(RestingHeartRateRecord::class, range)
            ).records
            records.lastOrNull()?.beatsPerMinute?.toInt() ?: 0
        } catch (e: Exception) { 0 }
    }

    private suspend fun readSteps(range: TimeRangeFilter): Int {
        return try {
            val result = client.aggregate(
                AggregateRequest(
                    metrics = setOf(StepsRecord.COUNT_TOTAL),
                    timeRangeFilter = range
                )
            )
            result[StepsRecord.COUNT_TOTAL]?.toInt() ?: 0
        } catch (e: Exception) { 0 }
    }

    private suspend fun readActiveCalories(range: TimeRangeFilter): Int {
        return try {
            val result = client.aggregate(
                AggregateRequest(
                    metrics = setOf(ActiveCaloriesBurnedRecord.ACTIVE_CALORIES_TOTAL),
                    timeRangeFilter = range
                )
            )
            result[ActiveCaloriesBurnedRecord.ACTIVE_CALORIES_TOTAL]?.inKilocalories?.toInt() ?: 0
        } catch (e: Exception) { 0 }
    }

    private suspend fun readTotalCalories(range: TimeRangeFilter): Int {
        return try {
            val result = client.aggregate(
                AggregateRequest(
                    metrics = setOf(TotalCaloriesBurnedRecord.ENERGY_TOTAL),
                    timeRangeFilter = range
                )
            )
            result[TotalCaloriesBurnedRecord.ENERGY_TOTAL]?.inKilocalories?.toInt() ?: 0
        } catch (e: Exception) { 0 }
    }

    private suspend fun readDistance(range: TimeRangeFilter): Double {
        return try {
            val result = client.aggregate(
                AggregateRequest(
                    metrics = setOf(DistanceRecord.DISTANCE_TOTAL),
                    timeRangeFilter = range
                )
            )
            (result[DistanceRecord.DISTANCE_TOTAL]?.inKilometers ?: 0.0)
        } catch (e: Exception) { 0.0 }
    }

    private suspend fun readLatestSpo2(range: TimeRangeFilter): Double {
        return try {
            val records = client.readRecords(
                ReadRecordsRequest(OxygenSaturationRecord::class, range)
            ).records
            records.lastOrNull()?.percentage?.value ?: 0.0
        } catch (e: Exception) { 0.0 }
    }

    private suspend fun readLatestBodyTemp(range: TimeRangeFilter): Double {
        return try {
            val records = client.readRecords(
                ReadRecordsRequest(BodyTemperatureRecord::class, range)
            ).records
            records.lastOrNull()?.temperature?.inCelsius ?: 0.0
        } catch (e: Exception) { 0.0 }
    }

    private suspend fun readLatestRespiratoryRate(range: TimeRangeFilter): Double {
        return try {
            val records = client.readRecords(
                ReadRecordsRequest(RespiratoryRateRecord::class, range)
            ).records
            records.lastOrNull()?.rate ?: 0.0
        } catch (e: Exception) { 0.0 }
    }

    private suspend fun readLastNightSleep(): DailyMetrics {
        return try {
            val now       = Instant.now()
            val yesterday = now.minus(24, ChronoUnit.HOURS)
            val range     = TimeRangeFilter.between(yesterday, now)
            val sessions  = client.readRecords(
                ReadRecordsRequest(SleepSessionRecord::class, range)
            ).records

            if (sessions.isEmpty()) return DailyMetrics()

            val session = sessions.last()
            var deep = 0; var rem = 0; var light = 0; var awake = 0
            session.stages.forEach { stage ->
                val mins = Duration.between(stage.startTime, stage.endTime).toMinutes().toInt()
                when (stage.stage) {
                    SleepSessionRecord.STAGE_TYPE_DEEP    -> deep += mins
                    SleepSessionRecord.STAGE_TYPE_REM     -> rem += mins
                    SleepSessionRecord.STAGE_TYPE_LIGHT   -> light += mins
                    SleepSessionRecord.STAGE_TYPE_AWAKE   -> awake += mins
                }
            }

            val totalAsleep = deep + rem + light
            val totalInBed  = totalAsleep + awake
            val efficiency  = if (totalInBed > 0) (totalAsleep.toDouble() / totalInBed * 100) else 0.0

            // Sleep score: weighted average of duration, efficiency, and deep sleep
            val durationScore  = (totalAsleep.toDouble() / 480 * 40).coerceIn(0.0, 40.0)
            val efficiencyScore = efficiency * 0.3
            val deepScore      = (deep.toDouble() / 90 * 30).coerceIn(0.0, 30.0)
            val sleepScore     = (durationScore + efficiencyScore + deepScore).toInt().coerceIn(0, 100)

            DailyMetrics(
                sleepScore        = sleepScore,
                totalSleepMinutes = totalAsleep,
                deepSleepMinutes  = deep,
                remSleepMinutes   = rem,
                lightSleepMinutes = light,
                awakeMinutes      = awake,
                sleepEfficiency   = efficiency
            )
        } catch (e: Exception) { DailyMetrics() }
    }

    private suspend fun getHrvBaseline(): Double {
        return try {
            val end   = Instant.now()
            val start = end.minus(30, ChronoUnit.DAYS)
            val range = TimeRangeFilter.between(start, end)
            val records = client.readRecords(
                ReadRecordsRequest(HeartRateVariabilityRmssdRecord::class, range)
            ).records
            if (records.isEmpty()) return 0.0
            records.map { it.heartRateVariabilityMillis }.average()
        } catch (e: Exception) { 0.0 }
    }

    private suspend fun getRestingHrBaseline(): Int {
        return try {
            val end   = Instant.now()
            val start = end.minus(30, ChronoUnit.DAYS)
            val range = TimeRangeFilter.between(start, end)
            val records = client.readRecords(
                ReadRecordsRequest(RestingHeartRateRecord::class, range)
            ).records
            if (records.isEmpty()) return 0
            records.map { it.beatsPerMinute.toInt() }.average().toInt()
        } catch (e: Exception) { 0 }
    }

    private suspend fun computeStrainFromExercise(range: TimeRangeFilter): Double {
        return try {
            val sessions = client.readRecords(
                ReadRecordsRequest(ExerciseSessionRecord::class, range)
            ).records
            if (sessions.isEmpty()) return 0.0

            var totalStrain = 0.0
            sessions.forEach { session ->
                val durationMins = Duration.between(session.startTime, session.endTime).toMinutes()
                // Without HR zone data, estimate based on activity type and duration
                val intensityMultiplier = when (session.exerciseType) {
                    ExerciseSessionRecord.EXERCISE_TYPE_RUNNING        -> 3.5
                    ExerciseSessionRecord.EXERCISE_TYPE_HIGH_INTENSITY_INTERVAL_TRAINING -> 5.0
                    ExerciseSessionRecord.EXERCISE_TYPE_STRENGTH_TRAINING -> 2.5
                    ExerciseSessionRecord.EXERCISE_TYPE_WALKING        -> 1.0
                    ExerciseSessionRecord.EXERCISE_TYPE_CYCLING        -> 3.0
                    else                                               -> 2.0
                }
                totalStrain += (durationMins * intensityMultiplier / 30.0)
            }
            totalStrain.coerceIn(0.0, 21.0)
        } catch (e: Exception) { 0.0 }
    }

    suspend fun getRecentWorkouts(days: Int = 7): List<WorkoutSession> {
        return try {
            val end   = Instant.now()
            val start = end.minus(days.toLong(), ChronoUnit.DAYS)
            val range = TimeRangeFilter.between(start, end)

            val sessions = client.readRecords(
                ReadRecordsRequest(ExerciseSessionRecord::class, range)
            ).records

            sessions.map { session ->
                val durationMins = Duration.between(session.startTime, session.endTime).toMinutes().toInt()
                val type = when (session.exerciseType) {
                    ExerciseSessionRecord.EXERCISE_TYPE_RUNNING         -> WorkoutType.RUNNING
                    ExerciseSessionRecord.EXERCISE_TYPE_STRENGTH_TRAINING -> WorkoutType.STRENGTH
                    ExerciseSessionRecord.EXERCISE_TYPE_CYCLING         -> WorkoutType.CYCLING
                    ExerciseSessionRecord.EXERCISE_TYPE_WALKING         -> WorkoutType.WALKING
                    ExerciseSessionRecord.EXERCISE_TYPE_HIGH_INTENSITY_INTERVAL_TRAINING -> WorkoutType.HIIT
                    ExerciseSessionRecord.EXERCISE_TYPE_SWIMMING_OPEN_WATER,
                    ExerciseSessionRecord.EXERCISE_TYPE_SWIMMING_POOL   -> WorkoutType.SWIMMING
                    else                                                -> WorkoutType.OTHER
                }
                WorkoutSession(
                    id             = session.metadata.id,
                    title          = session.title ?: type.displayName(),
                    type           = type,
                    startTime      = session.startTime,
                    endTime        = session.endTime,
                    durationMinutes = durationMins,
                    calories       = 0,  // fetched separately if needed
                    avgHeartRate   = 0,
                    maxHeartRate   = 0,
                    source         = packageToFriendlyName(session.metadata.dataOrigin.packageName)
                )
            }.sortedByDescending { it.startTime }
        } catch (e: Exception) { emptyList() }
    }

    fun getHrvTrend(days: Int = 7): Flow<List<HrvDataPoint>> = flow {
        try {
            val end   = Instant.now()
            val start = end.minus(days.toLong(), ChronoUnit.DAYS)
            val range = TimeRangeFilter.between(start, end)
            val records = client.readRecords(
                ReadRecordsRequest(HeartRateVariabilityRmssdRecord::class, range)
            ).records

            // Group by date and average
            val byDate = records.groupBy {
                it.time.atZone(ZoneId.systemDefault()).toLocalDate()
            }
            val dataPoints = byDate.entries
                .sortedBy { it.key }
                .map { (date, recs) ->
                    HrvDataPoint(date, recs.map { it.heartRateVariabilityMillis }.average())
                }
            emit(dataPoints)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }
}
