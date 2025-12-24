package com.lssgoo.planner.features.calendar.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lssgoo.planner.util.KmpDateFormatter
import com.lssgoo.planner.util.KmpTimeUtils
import kotlinx.datetime.*

@Composable
fun CalendarGrid(
    year: Int,
    month: Int,
    selectedDate: Long,
    activityCounts: Map<Int, Int>,
    onDateSelected: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val daysOfWeek = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
    val firstDayOfWeek = KmpDateFormatter.getFirstDayOfMonth(year, month)
    val daysInMonth = KmpDateFormatter.getDaysInMonth(year, month)
    
    val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    val isCurrentMonth = year == now.year && month == now.monthNumber
    
    Column(modifier = modifier) {
        // Days of week header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            daysOfWeek.forEach { day ->
                Text(
                    text = day,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Calendar days
        val totalCells = firstDayOfWeek + daysInMonth
        val rows = (totalCells + 6) / 7
        
        for (row in 0 until rows) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                for (col in 0..6) {
                    val cellIndex = row * 7 + col
                    val day = cellIndex - firstDayOfWeek + 1
                    
                    if (day in 1..daysInMonth) {
                        val dayTimestamp = KmpDateFormatter.getTimestampForDay(year, month, day)
                        val isSelected = KmpDateFormatter.isSameDay(dayTimestamp, selectedDate)
                        val isToday = isCurrentMonth && day == now.dayOfMonth
                        val count = activityCounts[day] ?: 0
                        
                        CalendarDay(
                            day = day,
                            isSelected = isSelected,
                            isToday = isToday,
                            activityCount = count,
                            onClick = { onDateSelected(dayTimestamp) },
                            modifier = Modifier.weight(1f)
                        )
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

@Composable
fun CalendarDay(
    day: Int,
    isSelected: Boolean,
    isToday: Boolean,
    activityCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .padding(2.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(
                when {
                    isSelected -> colorScheme.primary
                    isToday -> colorScheme.primaryContainer.copy(alpha = 0.5f)
                    else -> Color.Transparent
                }
            )
            .then(
                if (isToday && !isSelected) {
                    Modifier.border(1.dp, colorScheme.primary.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                } else Modifier
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = day.toString(),
                style = typography.bodyLarge,
                fontWeight = if (isSelected || isToday) FontWeight.ExtraBold else FontWeight.Medium,
                color = when {
                    isSelected -> colorScheme.onPrimary
                    else -> colorScheme.onSurface
                }
            )
            
            if (activityCount > 0) {
                Surface(
                    shape = CircleShape,
                    color = if (isSelected) Color.White.copy(alpha = 0.3f) else colorScheme.primary.copy(alpha = 0.1f),
                    modifier = Modifier.size(16.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = activityCount.toString(),
                            style = typography.labelSmall.copy(
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = if (isSelected) Color.White else colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}
