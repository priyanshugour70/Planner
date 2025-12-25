package com.lssgoo.planner.features.finance.utils

import com.lssgoo.planner.features.finance.models.DateRange
import com.lssgoo.planner.features.finance.models.DateRangeFilter
import com.lssgoo.planner.util.KmpTimeUtils
import kotlinx.datetime.*

/**
 * Utility object for finance date operations
 */
object FinanceDateUtils {
    
    /**
     * Get date range based on filter type
     */
    fun getDateRange(filter: DateRangeFilter, customStart: Long? = null, customEnd: Long? = null): DateRange {
        val now = Clock.System.now()
        val today = now.toLocalDateTime(TimeZone.currentSystemDefault()).date
        
        return when (filter) {
            DateRangeFilter.TODAY -> {
                val start = today.atStartOfDayIn(TimeZone.currentSystemDefault()).toEpochMilliseconds()
                val end = today.plus(1, DateTimeUnit.DAY).atStartOfDayIn(TimeZone.currentSystemDefault()).toEpochMilliseconds()
                DateRange(start, end, filter)
            }
            DateRangeFilter.THIS_WEEK -> {
                val startOfWeek = today.minus(today.dayOfWeek.ordinal, DateTimeUnit.DAY)
                val start = startOfWeek.atStartOfDayIn(TimeZone.currentSystemDefault()).toEpochMilliseconds()
                val end = startOfWeek.plus(7, DateTimeUnit.DAY).atStartOfDayIn(TimeZone.currentSystemDefault()).toEpochMilliseconds()
                DateRange(start, end, filter)
            }
            DateRangeFilter.THIS_MONTH -> {
                val startOfMonth = LocalDate(today.year, today.month, 1)
                val start = startOfMonth.atStartOfDayIn(TimeZone.currentSystemDefault()).toEpochMilliseconds()
                val endOfMonth = startOfMonth.plus(1, DateTimeUnit.MONTH)
                val end = endOfMonth.atStartOfDayIn(TimeZone.currentSystemDefault()).toEpochMilliseconds()
                DateRange(start, end, filter)
            }
            DateRangeFilter.THIS_YEAR -> {
                val startOfYear = LocalDate(today.year, Month.JANUARY, 1)
                val start = startOfYear.atStartOfDayIn(TimeZone.currentSystemDefault()).toEpochMilliseconds()
                val endOfYear = LocalDate(today.year + 1, Month.JANUARY, 1)
                val end = endOfYear.atStartOfDayIn(TimeZone.currentSystemDefault()).toEpochMilliseconds()
                DateRange(start, end, filter)
            }
            DateRangeFilter.CUSTOM -> {
                DateRange(
                    customStart ?: KmpTimeUtils.currentTimeMillis(),
                    customEnd ?: KmpTimeUtils.currentTimeMillis(),
                    filter
                )
            }
        }
    }
    
    /**
     * Check if a timestamp falls within a date range
     */
    fun isInRange(timestamp: Long, range: DateRange): Boolean {
        return timestamp >= range.startDate && timestamp < range.endDate
    }
    
    /**
     * Format date range for display
     */
    fun formatDateRange(range: DateRange): String {
        return when (range.filter) {
            DateRangeFilter.CUSTOM -> {
                val start = com.lssgoo.planner.util.KmpDateFormatter.formatDate(range.startDate)
                val end = com.lssgoo.planner.util.KmpDateFormatter.formatDate(range.endDate)
                "$start - $end"
            }
            else -> range.filter.displayName
        }
    }
}
