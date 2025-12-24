package com.lssgoo.planner.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.lssgoo.planner.ui.navigation.BottomNavDestination
import com.lssgoo.planner.ui.navigation.Routes
import com.lssgoo.planner.ui.viewmodel.PlannerViewModel
import com.lssgoo.planner.features.dashboard.screens.DashboardScreen
import com.lssgoo.planner.features.goals.screens.GoalsScreen
import com.lssgoo.planner.features.goals.screens.GoalDetailScreen
import com.lssgoo.planner.features.tasks.screens.TasksScreen
import com.lssgoo.planner.features.habits.screens.HabitsScreen
import com.lssgoo.planner.features.journal.screens.JournalScreen
import com.lssgoo.planner.features.notes.screens.NotesScreen
import com.lssgoo.planner.features.notes.screens.NoteDetailScreen
import com.lssgoo.planner.features.calendar.screens.CalendarScreen
import com.lssgoo.planner.features.settings.screens.SettingsScreen
import com.lssgoo.planner.features.search.screens.SearchScreen
import com.lssgoo.planner.features.reminders.screens.RemindersScreen
import com.lssgoo.planner.features.finance.screens.FinanceScreen

@Composable
fun MainScreen(viewModel: PlannerViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            if (shouldShowBottomBar(currentRoute)) {
                com.lssgoo.planner.ui.components.DynamicBottomNavBar(navController, currentRoute)
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Routes.DASHBOARD,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Routes.DASHBOARD) {
                DashboardScreen(
                    viewModel = viewModel,
                    onGoalClick = { goalId -> navController.navigate(Routes.goalDetail(goalId)) },
                    onViewAllGoals = { navController.navigate(Routes.GOALS) },
                    onViewAllTasks = { navController.navigate(Routes.TASKS) },
                    onViewAllHabits = { navController.navigate(Routes.HABITS) },
                    onViewAllJournal = { navController.navigate(Routes.JOURNAL) },
                    onViewAllNotes = { navController.navigate(Routes.NOTES) },
                    onSearchClick = { navController.navigate(Routes.SEARCH) }
                )
            }
            
            composable(Routes.GOALS) {
                GoalsScreen(
                    viewModel = viewModel,
                    onGoalClick = { goalId -> navController.navigate(Routes.goalDetail(goalId)) }
                )
            }
            
            composable(Routes.GOAL_DETAIL) { backStackEntry ->
                val goalId = backStackEntry.arguments?.getString("goalId") ?: ""
                GoalDetailScreen(
                    goalId = goalId,
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }
            
            composable(Routes.TASKS) {
                TasksScreen(viewModel = viewModel)
            }
            
            composable(Routes.HABITS) {
                HabitsScreen(
                    viewModel = viewModel,
                    onHabitClick = { habitId -> navController.navigate(Routes.habitDetail(habitId)) }
                )
            }
            
            composable(Routes.JOURNAL) {
                JournalScreen(
                    viewModel = viewModel,
                    onEntryClick = { entryId -> navController.navigate(Routes.journalEntry(entryId)) }
                )
            }
            
            composable(Routes.NOTES) {
                NotesScreen(
                    viewModel = viewModel
                )
            }
            
            composable(Routes.NOTE_DETAIL) { backStackEntry ->
                val noteId = backStackEntry.arguments?.getString("noteId") ?: ""
                NoteDetailScreen(
                    noteId = noteId,
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }
            
            composable(Routes.NOTE_CREATE) {
                // NotesScreen handles its own creation via sheets, 
                // but if we need a separate create screen, we'd need to update NoteDetailScreen
                NotesScreen(viewModel = viewModel)
            }
            
            composable(Routes.CALENDAR) {
                CalendarScreen(viewModel = viewModel)
            }
            
            composable(Routes.SETTINGS) {
                SettingsScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onNavigateToPin = { navController.navigate(Routes.APPLOCK) },
                    onNavigate = { route -> navController.navigate(route) }
                )
            }
            
            composable(Routes.SEARCH) {
                SearchScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onResultClick = { route -> navController.navigate(route) }
                )
            }

            composable(Routes.REMINDERS) {
                 RemindersScreen(
                     viewModel = viewModel,
                     onBack = { navController.popBackStack() }
                 )
            }

            composable(Routes.FINANCE) {
                FinanceScreen(
                    viewModel = viewModel
                )
            }

            // Additional routes can be added here (Analytics, Finance, etc.)
        }
    }
}


fun shouldShowBottomBar(route: String?): Boolean {
    return route in BottomNavDestination.entries.map { it.route }
}
