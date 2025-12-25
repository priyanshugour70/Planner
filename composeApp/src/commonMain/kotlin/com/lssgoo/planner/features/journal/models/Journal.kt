package com.lssgoo.planner.features.journal.models

import kotlinx.serialization.Serializable
import com.lssgoo.planner.util.KmpIdGenerator

/**
 * Journal entry for daily reflection and tracking
 */
@Serializable
data class JournalEntry(
    val id: String = KmpIdGenerator.generateId(),
    val date: Long,
    val title: String = "",
    val content: String,
    val mood: JournalMood = JournalMood.NEUTRAL,
    val tags: List<String> = emptyList(),
    val linkedGoalIds: List<String> = emptyList(),
    val linkedTaskIds: List<String> = emptyList(),
    val photos: List<String> = emptyList(), // URIs
    val gratitude: List<String> = emptyList(),
    val achievements: List<String> = emptyList(),
    val challenges: List<String> = emptyList(),
    val reflection: String = "",
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L,
    val isDeleted: Boolean = false
)

@Serializable
enum class JournalMood(val emoji: String, val color: Long) {
    EXCELLENT("😄", 0xFF26C6DA),  // Light Blue
    GOOD("🙂", 0xFF4DD0E1),      // Light Cyan
    NEUTRAL("😐", 0xFF0097A7),    // Teal Blue
    BAD("😔", 0xFF00ACC1),       // Cyan Blue
    TERRIBLE("😢", 0xFF0288D1)   // Blue
}

@Serializable
data class JournalPrompt(
    val id: String = KmpIdGenerator.generateId(),
    val text: String,
    val category: PromptCategory,
    val isUsed: Boolean = false
)

@Serializable
enum class PromptCategory {
    GRATITUDE,
    REFLECTION,
    GOAL_REVIEW,
    SELF_IMPROVEMENT,
    MOTIVATION
}

@Serializable
data class JournalStats(
    val totalEntries: Int = 0,
    val entriesThisMonth: Int = 0,
    val averageMood: Float = 0f,
    val longestStreak: Int = 0,
    val currentStreak: Int = 0,
    // Note: Pair is not easily serializable out of the box in some targets, 
    // but for simple cases it might work. Let's keep it for now.
    // val mostUsedTags: List<Pair<String, Int>> = emptyList()
)

