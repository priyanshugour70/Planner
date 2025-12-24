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

@Composable
fun App() {
    // Create the storage repository and ViewModel
    val storageRepository = remember { AppStorageRepository(createSettings()) }
    val viewModel = remember { PlannerViewModel(storageRepository) }
    
    val settings by viewModel.settings.collectAsState()
    val isOnboardingComplete by viewModel.isOnboardingComplete.collectAsState()
    val isCheckingSync by viewModel.isCheckingSync.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    
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
                // TODO: Show onboarding screen
                OnboardingPlaceholder(
                    onComplete = { profile ->
                        viewModel.saveUserProfile(profile)
                        viewModel.setOnboardingComplete()
                    }
                )
            } else {
                // Main app content
                MainScreen(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun OnboardingPlaceholder(onComplete: (com.lssgoo.planner.data.model.UserProfile) -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Welcome to Planner! 🎯",
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    onComplete(com.lssgoo.planner.data.model.UserProfile(firstName = "User"))
                }
            ) {
                Text("Get Started")
            }
        }
    }
}


