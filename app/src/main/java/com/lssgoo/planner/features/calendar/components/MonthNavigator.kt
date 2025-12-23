package com.lssgoo.planner.features.calendar.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lssgoo.planner.ui.components.AppIcons
import java.text.SimpleDateFormat
import java.util.Calendar

@Composable
fun MonthNavigator(
    currentMonth: Calendar,
    monthFormat: SimpleDateFormat,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onToday: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPreviousMonth) {
            Icon(AppIcons.ChevronLeft, contentDescription = "Previous month")
        }
        
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = monthFormat.format(currentMonth.time),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            TextButton(onClick = onToday) {
                Text("Today", style = MaterialTheme.typography.labelMedium)
            }
        }
        
        IconButton(onClick = onNextMonth) {
            Icon(AppIcons.ChevronRight, contentDescription = "Next month")
        }
    }
}
