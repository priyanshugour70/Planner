package com.lssgoo.planner.features.finance.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lssgoo.planner.features.finance.models.DateRange
import com.lssgoo.planner.features.finance.models.DateRangeFilter
import com.lssgoo.planner.features.finance.utils.FinanceDateUtils
import com.lssgoo.planner.util.KmpDateFormatter
import com.lssgoo.planner.util.KmpTimeUtils

/**
 * Date range selector component for finance filtering
 * Uses themed horizontal scrollable selector
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRangeSelector(
    currentRange: DateRange,
    onRangeSelected: (DateRange) -> Unit,
    modifier: Modifier = Modifier
) {
    var showCustomDatePicker by remember { mutableStateOf(false) }
    
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Period Label
        Text(
            text = "Period",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        
        // Horizontal Scrollable Filter Selector
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            DateRangeFilter.entries.forEach { filter ->
                val isSelected = currentRange.filter == filter
                Surface(
                    onClick = {
                        if (filter == DateRangeFilter.CUSTOM) {
                            showCustomDatePicker = true
                        } else {
                            onRangeSelected(FinanceDateUtils.getDateRange(filter))
                        }
                    },
                    shape = RoundedCornerShape(14.dp),
                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    border = if (isSelected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
                    modifier = Modifier.widthIn(min = 100.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        // Icon for each filter
                        Icon(
                            imageVector = when (filter) {
                                DateRangeFilter.TODAY -> Icons.Default.Today
                                DateRangeFilter.THIS_WEEK -> Icons.Default.DateRange
                                DateRangeFilter.THIS_MONTH -> Icons.Default.CalendarMonth
                                DateRangeFilter.THIS_YEAR -> Icons.Default.CalendarToday
                                DateRangeFilter.CUSTOM -> Icons.Default.Event
                            },
                            contentDescription = null,
                            tint = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            filter.displayName,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
        
        // Show selected date range
        if (currentRange.filter == DateRangeFilter.CUSTOM) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.DateRange,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = FinanceDateUtils.formatDateRange(currentRange),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
    
    if (showCustomDatePicker) {
        CustomDateRangeDialog(
            onDismiss = { showCustomDatePicker = false },
            onConfirm = { start, end ->
                onRangeSelected(FinanceDateUtils.getDateRange(DateRangeFilter.CUSTOM, start, end))
                showCustomDatePicker = false
            }
        )
    }
}

/**
 * Custom date range picker dialog
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDateRangeDialog(
    onDismiss: () -> Unit,
    onConfirm: (Long, Long) -> Unit
) {
    var startDate by remember { mutableStateOf<Long?>(null) }
    var endDate by remember { mutableStateOf<Long?>(null) }
    var showStartPicker by remember { mutableStateOf(false) }
    var showEndPicker by remember { mutableStateOf(false) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Custom Date Range") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedButton(
                    onClick = { showStartPicker = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.CalendarToday, null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = startDate?.let { KmpDateFormatter.formatDate(it) } ?: "Select Start Date"
                    )
                }
                
                OutlinedButton(
                    onClick = { showEndPicker = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Event, null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = endDate?.let { KmpDateFormatter.formatDate(it) } ?: "Select End Date"
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (startDate != null && endDate != null) {
                        onConfirm(startDate!!, endDate!!)
                    }
                },
                enabled = startDate != null && endDate != null
            ) {
                Text("Apply")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
    
    if (showStartPicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = startDate ?: KmpTimeUtils.currentTimeMillis()
        )
        DatePickerDialog(
            onDismissRequest = { showStartPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    startDate = datePickerState.selectedDateMillis
                    showStartPicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showStartPicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
    
    if (showEndPicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = endDate ?: KmpTimeUtils.currentTimeMillis()
        )
        DatePickerDialog(
            onDismissRequest = { showEndPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    endDate = datePickerState.selectedDateMillis
                    showEndPicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showEndPicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
