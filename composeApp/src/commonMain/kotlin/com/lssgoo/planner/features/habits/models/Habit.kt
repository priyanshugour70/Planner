package com.lssgoo.planner.features.habits.models

import kotlinx.serialization.Serializable
import com.lssgoo.planner.util.KmpIdGenerator
import com.lssgoo.planner.util.KmpTimeUtils

@Serializable
enum class HabitType(val displayName: String) {
    YES_NO("Yes/No"),           // Simple checkbox
    QUANTITATIVE("Quantitative"), // Count (e.g., 5 glasses of water)
    TIMER("Timer")               // Duration (e.g., 10 mins meditation)
}

@Serializable
enum class HabitTimeOfDay(val displayName: String) {
    ANY_TIME("Any Time"),
    MORNING("Morning Routine"),
    AFTERNOON("Afternoon Grind"),
    EVENING("Evening Wind-down")
}

@Serializable
data class Habit(
    val id: String = KmpIdGenerator.generateId(),
    val goalId: String?, // Nullable if not linked to a specific goal
    val title: String,
    val description: String = "",
    val icon: String = "✨",
    val iconColor: Long = 0xFF4DD0E1, // Hex color
    val type: HabitType = HabitType.YES_NO,
    val targetValue: Float = 1f, // e.g., 10 (pages), 15 (mins)
    val unit: String? = null,    // e.g., "pages", "mins", "glasses"
    val frequency: List<Int> = listOf(1, 2, 3, 4, 5, 6, 7), // Days of week (1=Mon, 7=Sun)
    val timeOfDay: HabitTimeOfDay = HabitTimeOfDay.ANY_TIME,
    val reminderTime: String? = null,
    val isActive: Boolean = true,
    val createdAt: Long = KmpTimeUtils.currentTimeMillis()
)

// NOTE: HabitEntry here conflicts with Task.kt's HabitEntry?
// Task.kt has HabitEntry as well. We should consolidate or rename one.
// The one in Task.kt seems minimal. This one has more details.
// For now, let's keep this one as `DetailedHabitEntry` or resolve conflict later.
// But wait, Task.kt imported HabitEntry from here? No, it defined it.
// I will keep this data class here as it seems to be the primary definition.
// Wait, Task.kt's definition:
// data class HabitEntry(id, date, goalId, isCompleted, notes)
// This one:
// data class HabitEntry(id, habitId, date, value, isCompleted, mood, notes)
// They are different. Task.kt's HabitEntry seems to be a conflict. 
// However, since I am editing files in place in commonMain, I should unify them.
// I'll keep this one as it matches the Habit feature better.

@Serializable
data class HabitEntry(
    val id: String = KmpIdGenerator.generateId(),
    val habitId: String,
    val date: Long, // Processed to start of day
    val value: Float = 0f, // For quant/timer (current progress)
    val isCompleted: Boolean = false,
    val mood: HabitMood? = null,
    val notes: String = ""
)

@Serializable
enum class HabitMood(val emoji: String, val label: String) {
    EXCELLENT("🤩", "Excellent"),
    GOOD("🙂", "Good"),
    NEUTRAL("😐", "Neutral"),
    TIRED("😴", "Tired"),
    STRESSED("😫", "Stressed")
}

@Serializable
data class HabitStats(
    val habitId: String,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val totalCompletions: Int = 0,
    val completionRate: Float = 0f,
    val consistencyScore: Int = 0, // 0-100 score
    val last7Days: List<Boolean> = emptyList(), // For rings/mini-charts
    val dayPerformance: Map<Int, Float> = emptyMap(), // DayOfWeek (1-7) -> Rate (0.0-1.0)
    val heatmapData: Map<Long, Int> = emptyMap() // Date -> Level (0-4) for GitHub style graph
)
