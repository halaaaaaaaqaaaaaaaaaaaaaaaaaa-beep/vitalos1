package com.vitalos.app.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vitalos.app.ui.theme.*
import com.vitalos.app.viewmodel.DashboardViewModel

sealed class NavTab(val label: String, val icon: ImageVector) {
    object Today    : NavTab("Today",    Icons.Filled.Home)
    object Health   : NavTab("Health",   Icons.Filled.Favorite)
    object Activity : NavTab("Activity", Icons.Filled.DirectionsRun)
    object Sleep    : NavTab("Sleep",    Icons.Filled.Bedtime)
    object Trends   : NavTab("Trends",   Icons.Filled.ShowChart)
}

@Composable
fun MainScreen(
    viewModel: DashboardViewModel,
    onRequestPermissions: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedTab by remember { mutableStateOf<NavTab>(NavTab.Today) }

    val tabs = listOf(
        NavTab.Today,
        NavTab.Health,
        NavTab.Activity,
        NavTab.Sleep,
        NavTab.Trends
    )

    // Show permission / install screen if needed
    if (uiState.healthStatus.isAvailable == false && !uiState.isLoading) {
        HealthConnectSetupScreen(onRequestPermissions = onRequestPermissions)
        return
    }
    if (uiState.healthStatus.isAvailable && !uiState.healthStatus.hasPermissions) {
        PermissionsScreen(onRequestPermissions = onRequestPermissions)
        return
    }

    Scaffold(
        containerColor = BackgroundPrimary,
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xF00A0A0A),
                tonalElevation = 0.dp
            ) {
                tabs.forEach { tab ->
                    NavigationBarItem(
                        selected = selectedTab == tab,
                        onClick  = { selectedTab = tab },
                        icon     = {
                            Icon(
                                imageVector  = tab.icon,
                                contentDescription = tab.label
                            )
                        },
                        label = {
                            Text(
                                text  = tab.label,
                                style = MaterialTheme.typography.labelSmall
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor   = StrainRed,
                            selectedTextColor   = StrainRed,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary,
                            indicatorColor      = Color(0x22FF4F4F)
                        )
                    )
                }
            }
        }
    ) { paddingValues ->
        AnimatedContent(
            targetState = selectedTab,
            modifier = Modifier.padding(paddingValues)
        ) { tab ->
            when (tab) {
                NavTab.Today    -> TodayScreen(uiState = uiState, onRefresh = viewModel::refresh)
                NavTab.Health   -> HealthScreen(uiState = uiState)
                NavTab.Activity -> ActivityScreen(uiState = uiState)
                NavTab.Sleep    -> SleepScreen(uiState = uiState)
                NavTab.Trends   -> TrendsScreen(uiState = uiState)
            }
        }
    }
}
