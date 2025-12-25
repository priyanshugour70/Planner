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
    
    // Repositories
    val goalRepository = remember { com.lssgoo.planner.data.repository.GoalRepositoryImpl(storageRepository) }
    val taskRepository = remember { com.lssgoo.planner.data.repository.TaskRepositoryImpl(storageRepository) }
    val noteRepository = remember { com.lssgoo.planner.data.repository.NoteRepositoryImpl(storageRepository) }
    val habitRepository = remember { com.lssgoo.planner.data.repository.HabitRepositoryImpl(storageRepository) }
    val journalRepository = remember { com.lssgoo.planner.data.repository.JournalRepositoryImpl(storageRepository) }
    val financeRepository = remember { com.lssgoo.planner.data.repository.FinanceRepositoryImpl(storageRepository) }
    val reminderRepository = remember { com.lssgoo.planner.data.repository.ReminderRepositoryImpl(storageRepository) }
    val settingsRepository = remember { com.lssgoo.planner.data.repository.SettingsRepositoryImpl(storageRepository) }
    val userRepository = remember { com.lssgoo.planner.data.repository.UserRepositoryImpl(storageRepository) }

    val viewModel = remember { 
        PlannerViewModel(
            storageManager = storageRepository,
            goalRepository = goalRepository,
            taskRepository = taskRepository,
            noteRepository = noteRepository,
            habitRepository = habitRepository,
            journalRepository = journalRepository,
            financeRepository = financeRepository,
            reminderRepository = reminderRepository,
            settingsRepository = settingsRepository,
            userRepository = userRepository
        ) 
    }
    
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




