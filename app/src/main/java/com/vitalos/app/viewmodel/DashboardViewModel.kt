package com.vitalos.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.vitalos.app.data.health.DailyMetrics
import com.vitalos.app.data.health.HealthConnectStatus
import com.vitalos.app.data.health.HrvDataPoint
import com.vitalos.app.data.health.WorkoutSession
import com.vitalos.app.data.repository.HealthConnectRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class DashboardUiState(
    val isLoading: Boolean                = true,
    val healthStatus: HealthConnectStatus = HealthConnectStatus(false, false, emptyList()),
    val metrics: DailyMetrics            = DailyMetrics(),
    val recentWorkouts: List<WorkoutSession> = emptyList(),
    val hrvTrend: List<HrvDataPoint>     = emptyList(),
    val error: String?                   = null,
    val lastRefreshed: Long              = 0L
)

class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = HealthConnectRepository(application)

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    val requiredPermissions get() = repo.requiredPermissions

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val status = repo.checkAvailability()
                _uiState.update { it.copy(healthStatus = status) }

                if (status.isAvailable && status.hasPermissions) {
                    val metrics  = repo.getTodayMetrics()
                    val workouts = repo.getRecentWorkouts()
                    val trend    = repo.getHrvTrend().firstOrNull() ?: emptyList()

                    _uiState.update {
                        it.copy(
                            isLoading      = false,
                            metrics        = metrics,
                            recentWorkouts = workouts,
                            hrvTrend       = trend,
                            lastRefreshed  = System.currentTimeMillis()
                        )
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false) }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, error = e.message ?: "Failed to load data")
                }
            }
        }
    }

    fun onPermissionsGranted() {
        refresh()
    }
}
