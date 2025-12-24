package com.lssgoo.planner.util

import kotlinx.datetime.*
import kotlin.random.Random

object KmpIdGenerator {
    fun generateId(): String {
        val chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        return (1..16)
            .map { chars[Random.nextInt(chars.length)] }
            .joinToString("")
    }
}

object KmpTimeUtils {
    fun currentTimeMillis(): Long {
        return Clock.System.now().toEpochMilliseconds()
    }
    
    fun getStartOfDay(timestamp: Long): Long {
         val instant = Instant.fromEpochMilliseconds(timestamp)
         val tz = TimeZone.currentSystemDefault()
         val localDateTime = instant.toLocalDateTime(tz)
         val startOfDay = LocalDateTime(
             year = localDateTime.year,
             month = localDateTime.month,
             dayOfMonth = localDateTime.dayOfMonth,
             hour = 0,
             minute = 0,
             second = 0,
             nanosecond = 0
         )
         return startOfDay.toInstant(tz).toEpochMilliseconds()
    }
}

/**
 * Multiplatform date formatting utilities
 */
object KmpDateFormatter {
    
    private val daysOfWeek = listOf("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")
    private val months = listOf("January", "February", "March", "April", "May", "June", 
                                "July", "August", "September", "October", "November", "December")
    private val monthsShort = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", 
                                     "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
    
    /**
     * Format: "Monday, 24 December 2024"
     */
    fun formatFullDate(timestamp: Long): String {
        val instant = Instant.fromEpochMilliseconds(timestamp)
        val dateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
        val dayOfWeek = daysOfWeek[dateTime.dayOfWeek.ordinal]
        val month = months[dateTime.monthNumber - 1]
        return "$dayOfWeek, ${dateTime.dayOfMonth} $month ${dateTime.year}"
    }
    
    /**
     * Format: "Dec 24"
     */
    fun formatShortDate(timestamp: Long): String {
        val instant = Instant.fromEpochMilliseconds(timestamp)
        val dateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
        val month = monthsShort[dateTime.monthNumber - 1]
        return "$month ${dateTime.dayOfMonth}"
    }
    
    /**
     * Format: "Dec 24, 2024"
     */
    fun formatMediumDate(timestamp: Long): String {
        val instant = Instant.fromEpochMilliseconds(timestamp)
        val dateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
        val month = monthsShort[dateTime.monthNumber - 1]
        return "$month ${dateTime.dayOfMonth}, ${dateTime.year}"
    }
    
    /**
     * Format: "3:45 PM"
     */
    fun formatTime(timestamp: Long): String {
        val instant = Instant.fromEpochMilliseconds(timestamp)
        val dateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
        val hour12 = if (dateTime.hour == 0) 12 else if (dateTime.hour > 12) dateTime.hour - 12 else dateTime.hour
        val amPm = if (dateTime.hour < 12) "AM" else "PM"
        val minute = dateTime.minute.toString().padStart(2, '0')
        return "$hour12:$minute $amPm"
    }
    
    /**
     * Format: "Dec 24, 3:45 PM"
     */
    fun formatDateWithTime(timestamp: Long): String {
        return "${formatShortDate(timestamp)}, ${formatTime(timestamp)}"
    }

    /**
     * Format: "Dec 24, 2024"
     */
    fun formatDate(timestamp: Long): String {
        return formatMediumDate(timestamp)
    }

    /**
     * Format: "Dec 24, 2024, 3:45 PM"
     */
    fun formatDateTime(timestamp: Long): String {
        return "${formatMediumDate(timestamp)}, ${formatTime(timestamp)}"
    }

    /**
     * Format: "December 24, 2024 at 3:45 PM"
     */
    fun formatFullDateTime(timestamp: Long): String {
        val instant = Instant.fromEpochMilliseconds(timestamp)
        val dateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
        val month = months[dateTime.monthNumber - 1]
        return "$month ${dateTime.dayOfMonth}, ${dateTime.year} at ${formatTime(timestamp)}"
    }
    
    /**
     * Get current year
     */
    fun getCurrentYear(): Int {
        return Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).year
    }
    
    /**
     * Get day of year (1-366)
     */
    fun getDayOfYear(timestamp: Long = KmpTimeUtils.currentTimeMillis()): Int {
        val instant = Instant.fromEpochMilliseconds(timestamp)
        return instant.toLocalDateTime(TimeZone.currentSystemDefault()).dayOfYear
    }
    
    /**
     * Check if given year is a leap year
     */
    fun isLeapYear(year: Int): Boolean {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)
    }
    
    /**
     * Get days in current year
     */
    fun getDaysInYear(year: Int = getCurrentYear()): Int {
        return if (isLeapYear(year)) 366 else 365
    }

    /**
     * Format: "December 2024"
     */
    fun formatMonthYear(year: Int, month: Int): String {
        return "${months[month - 1]} $year"
    }

    /**
     * Get first day of week for a month (0 = Sunday, 6 = Saturday)
     */
    fun getFirstDayOfMonth(year: Int, month: Int): Int {
        val date = LocalDate(year, month, 1)
        // DayOfWeek: Monday is 1, Sunday is 7 in kotlinx-datetime
        // index: M=0, T=1, W=2, Th=3, F=4, S=5, Su=6
        // We want: Su=0, M=1, T=2, W=3, Th=4, F=5, S=6
        return (date.dayOfWeek.ordinal + 1) % 7
    }

    /**
     * Get number of days in a month
     */
    fun getDaysInMonth(year: Int, month: Int): Int {
        return when (month) {
            1, 3, 5, 7, 8, 10, 12 -> 31
            4, 6, 9, 11 -> 30
            2 -> if (isLeapYear(year)) 29 else 28
            else -> 30
        }
    }

    /**
     * Check if two timestamps are on the same day
     */
    fun isSameDay(t1: Long, t2: Long): Boolean {
        val tz = TimeZone.currentSystemDefault()
        val d1 = Instant.fromEpochMilliseconds(t1).toLocalDateTime(tz).date
        val d2 = Instant.fromEpochMilliseconds(t2).toLocalDateTime(tz).date
        return d1 == d2
    }

    /**
     * Get timestamp for a specific day in a month
     */
    fun getTimestampForDay(year: Int, month: Int, day: Int): Long {
        val tz = TimeZone.currentSystemDefault()
        return LocalDateTime(year, month, day, 0, 0, 0, 0).toInstant(tz).toEpochMilliseconds()
    }
}
