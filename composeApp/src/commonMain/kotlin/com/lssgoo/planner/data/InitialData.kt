package com.lssgoo.planner.data

import com.lssgoo.planner.data.model.*
import com.lssgoo.planner.features.habits.models.Habit
import com.lssgoo.planner.features.habits.models.HabitType
import com.lssgoo.planner.features.habits.models.HabitTimeOfDay
import com.lssgoo.planner.features.goals.models.DefaultGoals
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

object InitialData {

    // Goals are already defined in DefaultGoals, we can re-export or use directly
    fun getGoals(): List<Goal> = DefaultGoals.goals

    fun getHabits(): List<Habit> {
        return listOf(
            Habit(
                title = "Drink Water",
                description = "Stay hydrated",
                icon = "WaterDrop",
                iconColor = 0xFF2196F3,
                type = HabitType.QUANTITATIVE,
                targetValue = 8f,
                unit = "glasses",
                timeOfDay = HabitTimeOfDay.ANY_TIME,
                goalId = null // Add missing goalId
            ),
            Habit(
                title = "Morning Workout",
                description = "Start day with energy",
                icon = "FitnessCenter",
                iconColor = 0xFF4CAF50,
                type = HabitType.YES_NO,
                timeOfDay = HabitTimeOfDay.MORNING,
                goalId = null // Add missing goalId
            ),
            Habit(
                title = "Read Books",
                description = "Learn something new",
                icon = "MenuBook",
                iconColor = 0xFF9C27B0,
                type = HabitType.QUANTITATIVE,
                targetValue = 15f,
                unit = "pages",
                timeOfDay = HabitTimeOfDay.EVENING,
                goalId = null // Add missing goalId
            ),
             Habit(
                title = "Meditation",
                description = "Clear your mind",
                icon = "SelfImprovement",
                iconColor = 0xFF673AB7, // Deep Purple
                type = HabitType.YES_NO,
                timeOfDay = HabitTimeOfDay.MORNING,
                goalId = null // Add missing goalId
            ),
            Habit(
                title = "Walk 10k Steps",
                description = "Daily movement goal",
                icon = "DirectionsWalk",
                iconColor = 0xFFFF9800, // Orange
                type = HabitType.QUANTITATIVE,
                targetValue = 10000f,
                unit = "steps",
                timeOfDay = HabitTimeOfDay.ANY_TIME,
                goalId = null // Add missing goalId
            )
        )
    }

    fun getTasks(): List<Task> {
        val now = Clock.System.now().toEpochMilliseconds()
        return listOf(
            Task(
                title = "Welcome to Planner!",
                description = "Explore the app features and set up your profile.",
                priority = TaskPriority.HIGH,
                dueDate = now + 86400000, // Tomorrow
                isCompleted = false,
                tags = listOf("General")
            ),
            Task(
                title = "Review Weekly Goals",
                description = "Check progress on your 2026 goals.",
                priority = TaskPriority.MEDIUM,
                dueDate = now + (86400000 * 3),
                isCompleted = false,
                tags = listOf("Planning")
            ),
             Task(
                title = "Grocery Shopping",
                description = "Buy standard healthy foods.",
                priority = TaskPriority.LOW,
                dueDate = now + (86400000 * 2),
                isCompleted = false,
                tags = listOf("Personal")
            )
        )
    }

    fun getNotes(): List<Note> {
        val now = Clock.System.now().toEpochMilliseconds()
        return listOf(
            Note(
                title = "Planner Quick Start",
                content = "Welcome to your new Planner!\n\nThis app is designed to help you achieve your 2026 Vision.\n\nFeatures:\n- **Goals**: Track your 11 main life goals.\n- **Habits**: Build consistency.\n- **Finance**: Manage your budget.\n- **Tasks**: Get things done.\n\nEnjoy!",
                color = 0xFFFFF176, // Yellow note
                createdAt = now,
                updatedAt = now,
                category = "General"
            ),
            Note(
                title = "My Ideas",
                content = "Jot down your startup ideas here...",
                color = 0xFF81C784, // Green note
                createdAt = now,
                updatedAt = now,
                category = "Ideas"
            )
        )
    }

    fun getExampleBudgets(): List<Budget> {
        return listOf(
            Budget(
                category = TransactionCategory.FOOD,
                limitAmount = 5000.0,
                spentAmount = 0.0,
                period = BudgetPeriod.MONTHLY
            ),
            Budget(
                category = TransactionCategory.TRANSPORT,
                limitAmount = 2000.0,
                spentAmount = 0.0,
                period = BudgetPeriod.MONTHLY
            ),
             Budget(
                category = TransactionCategory.ENTERTAINMENT,
                limitAmount = 1500.0,
                spentAmount = 0.0,
                period = BudgetPeriod.MONTHLY
            ),
             Budget(
                category = TransactionCategory.SHOPPING,
                limitAmount = 3000.0,
                spentAmount = 0.0,
                period = BudgetPeriod.MONTHLY
            )
        )
    }

    fun getExampleTransactions(): List<Transaction> {
        val now = Clock.System.now().toEpochMilliseconds()
        return listOf(
            Transaction(
                amount = 150.0,
                type = TransactionType.EXPENSE,
                category = TransactionCategory.FOOD,
                note = "Healthy Lunch",
                date = now
            ),
            Transaction(
                amount = 25000.0,
                type = TransactionType.INCOME,
                category = TransactionCategory.SALARY,
                note = "Freelance Payment",
                date = now - 86400000 // Yesterday
            ),
             Transaction(
                amount = 500.0,
                type = TransactionType.EXPENSE,
                category = TransactionCategory.TRANSPORT,
                note = "Uber Ride",
                date = now - 43200000 
            )
        )
    }
}
