package com.lssgoo.planner.features.analytics.models

import kotlinx.serialization.Serializable
import com.lssgoo.planner.util.KmpIdGenerator
import com.lssgoo.planner.features.tasks.models.TaskPriority
import com.lssgoo.planner.features.goals.models.GoalCategory

/**
 * Analytics data models for comprehensive reporting
 */
@Serializable
data class AnalyticsData(
    val goalProgress: List<GoalProgressData> = emptyList(),
    val taskCompletion: TaskCompletionData = TaskCompletionData(),
    val habitStreaks: List<HabitStreakData> = emptyList(),
    val timeSeries: List<TimeSeriesData> = emptyList(),
    val categoryBreakdown: List<CategoryData> = emptyList(),
    val insights: List<Insight> = emptyList()
)

@Serializable
data class GoalProgressData(
    val goalId: String,
    val goalTitle: String,
    val progress: Float,
    val milestonesCompleted: Int,
    val totalMilestones: Int,
    val trend: TrendDirection
)

@Serializable
enum class TrendDirection {
    IMPROVING, DECLINING, STABLE
}

@Serializable
data class TaskCompletionData(
    val totalTasks: Int = 0,
    val completedTasks: Int = 0,
    val overdueTasks: Int = 0,
    val completionRate: Float = 0f,
    val averageCompletionTime: Long = 0, // in hours
    val byPriority: Map<TaskPriority, Int> = emptyMap(),
    val byCategory: Map<String, Int> = emptyMap()
)

@Serializable
data class HabitStreakData(
    val habitId: String,
    val habitTitle: String,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val completionRate: Float = 0f
)

@Serializable
data class TimeSeriesData(
    val date: Long,
    val tasksCompleted: Int = 0,
    val habitsCompleted: Int = 0,
    val milestonesCompleted: Int = 0
)

@Serializable
data class CategoryData(
    val category: GoalCategory,
    val goalsCount: Int = 0,
    val averageProgress: Float = 0f,
    val totalMilestones: Int = 0,
    val completedMilestones: Int = 0
)

@Serializable
data class Insight(
    val id: String = KmpIdGenerator.generateId(),
    val type: InsightType,
    val title: String,
    val message: String,
    val actionText: String? = null,
    val priority: InsightPriority = InsightPriority.MEDIUM
)

@Serializable
enum class InsightType {
    ACHIEVEMENT, WARNING, SUGGESTION, CELEBRATION
}

@Serializable
enum class InsightPriority {
    HIGH, MEDIUM, LOW
}

