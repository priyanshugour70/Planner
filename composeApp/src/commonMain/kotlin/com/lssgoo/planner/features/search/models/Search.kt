package com.lssgoo.planner.features.search.models

import kotlinx.serialization.Serializable
import com.lssgoo.planner.features.reminders.models.ItemPriority

/**
 * Search result model for global search
 */
@Serializable
data class SearchResult(
    val id: String,
    val title: String,
    val description: String,
    val type: SearchResultType,
    val highlightText: String, // Matched text with highlights
    val linkedGoalId: String? = null,
    val date: Long? = null,
    val priority: ItemPriority? = null
)

@Serializable
enum class SearchResultType(val displayName: String, val icon: String) {
    GOAL("Goal", "flag"),
    TASK("Task", "task"),
    NOTE("Note", "note"),
    EVENT("Event", "event"),
    REMINDER("Reminder", "alarm"),
    MILESTONE("Milestone", "milestone"),
    HABIT("Habit", "check_circle"),
    JOURNAL("Journal", "book"),
    FINANCE("Finance", "payments")
}

@Serializable
data class SearchFilters(
    val types: Set<SearchResultType> = emptySet(),
    val dateRange: DateRange? = null,
    val priority: ItemPriority? = null,
    val goalId: String? = null
)
// NOTE: Pair in SearchFilters might have serialization issues if not handled by a custom serializer, 
// seeing as we are in common code. But Pair<Long?, Long?> is simpler. 
// Standard Kotlin serialization should handle Pair in recent versions, or it acts as a list.
// If it fails, we can replace Pair with a custom Range class. 
// For now, I will rename it to a custom class to be safe and cleaner.

@Serializable
data class DateRange(val start: Long?, val end: Long?)

