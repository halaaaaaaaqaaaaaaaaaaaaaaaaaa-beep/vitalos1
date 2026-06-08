package com.vitalos.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import com.vitalos.app.ui.screens.MainScreen
import com.vitalos.app.ui.theme.BackgroundPrimary
import com.vitalos.app.ui.theme.VitalOSTheme
import com.vitalos.app.viewmodel.DashboardViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: DashboardViewModel by viewModels()

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { granted ->
        if (granted.values.any { it }) {
            viewModel.onPermissionsGranted()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            VitalOSTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = BackgroundPrimary
                ) {
                    MainScreen(
                        viewModel = viewModel,
                        onRequestPermissions = {
                            permissionLauncher.launch(
                                viewModel.requiredPermissions
                                    .map { it.toString() }
                                    .toTypedArray()
                            )
                        }
                    )
                }
            }
        }
    }
}
