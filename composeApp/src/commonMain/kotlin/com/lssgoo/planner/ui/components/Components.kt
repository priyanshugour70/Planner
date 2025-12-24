/**
 * This file is kept for backward compatibility.
 * All components have been moved to their proper locations:
 * 
 * - Generic UI components: ui/components/
 *   - AnimatedProgressBar.kt
 *   - GradientFAB.kt
 *   - EmptyState.kt
 *   - SectionHeader.kt
 *   - StatsCard.kt
 *   - AppIcons.kt
 * 
 * - Feature-specific components: features/<feature>/components/
 *   - GoalCard -> features/goals/components/GoalCard.kt
 *   - TaskItem -> features/tasks/components/TaskItem.kt
 *   - NoteCard -> features/notes/components/NoteCard.kt
 * 
 * This file can be deleted once all imports are updated.
 */
package com.lssgoo.planner.ui.components

// Re-export all components for backward compatibility
// New code should import directly from the specific files
