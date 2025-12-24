package com.lssgoo.planner

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lssgoo.planner.data.local.AppStorageRepository
import com.lssgoo.planner.di.createSettings
import com.lssgoo.planner.ui.theme.PlannerTheme
import com.lssgoo.planner.ui.viewmodel.PlannerViewModel
import com.lssgoo.planner.ui.MainScreen
import com.lssgoo.planner.features.onboarding.screens.OnboardingScreen

@Composable
fun App() {
    // Create the storage repository and ViewModel
    val storageRepository = remember { AppStorageRepository(createSettings()) }
    val viewModel = remember { PlannerViewModel(storageRepository) }
    
    val settings by viewModel.settings.collectAsState()
    val isOnboardingComplete by viewModel.isOnboardingComplete.collectAsState()
    val isCheckingSync by viewModel.isCheckingSync.collectAsState()
    
    PlannerTheme(themeMode = settings.themeMode) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            if (isCheckingSync) {
                // Show loading while checking for cloud backup
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (!isOnboardingComplete) {
                OnboardingScreen(viewModel = viewModel)
            } else {
                // Main app content
                MainScreen(viewModel = viewModel)
            }
        }
    }
}




