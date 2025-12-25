package com.lssgoo.planner.features.finance.models

import kotlinx.serialization.Serializable

/**
 * Date range filter for finance data
 */
@Serializable
enum class DateRangeFilter {
    TODAY,
    THIS_WEEK,
    THIS_MONTH,
    THIS_YEAR,
    CUSTOM;
    
    val displayName: String
        get() = when (this) {
            TODAY -> "Today"
            THIS_WEEK -> "This Week"
            THIS_MONTH -> "This Month"
            THIS_YEAR -> "This Year"
            CUSTOM -> "Custom Range"
        }
}

/**
 * Date range selection state
 */
@Serializable
data class DateRange(
    val startDate: Long,
    val endDate: Long,
    val filter: DateRangeFilter = DateRangeFilter.THIS_MONTH
)

/**
 * Savings goal model
 */
@Serializable
data class SavingsGoal(
    val id: String = com.lssgoo.planner.util.KmpIdGenerator.generateId(),
    val name: String,
    val targetAmount: Double,
    val currentAmount: Double = 0.0,
    val deadline: Long? = null,
    val category: String = "General",
    val color: Long = 0xFF4CAF50,
    val createdAt: Long = com.lssgoo.planner.util.KmpTimeUtils.currentTimeMillis(),
    val updatedAt: Long = com.lssgoo.planner.util.KmpTimeUtils.currentTimeMillis(),
    val isDeleted: Boolean = false
)

/**
 * Savings transaction model
 */
@Serializable
data class SavingsTransaction(
    val id: String = com.lssgoo.planner.util.KmpIdGenerator.generateId(),
    val savingsGoalId: String,
    val amount: Double,
    val type: SavingsTransactionType,
    val note: String = "",
    val date: Long = com.lssgoo.planner.util.KmpTimeUtils.currentTimeMillis(),
    val createdAt: Long = com.lssgoo.planner.util.KmpTimeUtils.currentTimeMillis()
)

@Serializable
enum class SavingsTransactionType {
    DEPOSIT,
    WITHDRAWAL
}
