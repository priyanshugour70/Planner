package com.lssgoo.planner.features.habits.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lssgoo.planner.features.habits.models.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.window.Dialog
import com.lssgoo.planner.util.KmpDateFormatter
import kotlinx.datetime.*

@Composable
fun HabitHeatmap(
    heatmapData: Map<Long, Int>,
    color: Color,
    modifier: Modifier = Modifier
) {
    // Generate last 100 days
    val days = remember {
        val list = mutableListOf<Long>()
        val tz = TimeZone.currentSystemDefault()
        var current = Clock.System.now().toLocalDateTime(tz).date
        
        repeat(98) { // 14 weeks * 7
            val startOfDay = LocalDateTime(current.year, current.month, current.dayOfMonth, 0, 0, 0, 0)
            list.add(startOfDay.toInstant(tz).toEpochMilliseconds())
            current = current.minus(1, DateTimeUnit.DAY)
        }
        list.reversed()
    }

    Column(modifier = modifier) {
        Text("Contribution Graph", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        LazyVerticalGrid(
            columns = GridCells.Fixed(14),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            userScrollEnabled = false,
            modifier = Modifier.height(140.dp) // Approx height
        ) {
            items(days) { date ->
                val level = heatmapData[date] ?: 0
                val alpha = when (level) {
                    0 -> 0.1f
                    1 -> 0.3f
                    2 -> 0.5f
                    3 -> 0.7f
                    else -> 1.0f
                }
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(color.copy(alpha = alpha))
                )
            }
        }
    }
}

@Composable
fun WeeklySuccessRings(
    last7Days: List<Boolean>, // Mon, Tue... Sun (or last 7 relative days)
    color: Color,
    modifier: Modifier = Modifier
) {
    val days = listOf("M", "T", "W", "T", "F", "S", "S")
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        last7Days.zip(days).forEach { (completed, day) ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        progress = { 1f },
                        modifier = Modifier.size(32.dp),
                        color = if (completed) color else color.copy(alpha = 0.2f),
                        trackColor = Color.Transparent,
                        strokeWidth = 3.dp
                    )
                    if (completed) {
                        Text("✓", fontSize = 12.sp, color = color, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(day, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun HabitDetailedItem(
    habit: Habit,
    stats: HabitStats,
    isCompletedToday: Boolean,
    onToggle: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val color = Color(habit.iconColor)
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(color.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(habit.icon, fontSize = 22.sp)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(habit.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    if (habit.targetValue > 1 || habit.type == HabitType.TIMER) {
                         Text(
                             "${habit.targetValue.toInt()} ${habit.unit ?: ""}", 
                             style = MaterialTheme.typography.labelSmall, 
                             color = MaterialTheme.colorScheme.onSurfaceVariant
                         )
                    } else {
                        Text(
                            habit.timeOfDay.displayName,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                // Toggle Button
                FilledIconButton(
                    onClick = onToggle,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = if (isCompletedToday) color else MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = if (isCompletedToday) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.size(44.dp)
                ) {
                    if (isCompletedToday) {
                        Text("✓", fontWeight = FontWeight.Bold)
                    } else {
                        // Show input icon for quant habits
                        val icon = when(habit.type) {
                            HabitType.YES_NO -> "○"
                            HabitType.QUANTITATIVE -> "+"
                            HabitType.TIMER -> "▶"
                        }
                        Text(icon, fontSize = 18.sp)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Stats Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatCompact("Streak", "${stats.currentStreak} days", "🔥")
                StatCompact("Rate", "${(stats.completionRate * 100).toInt()}%", "📊")
                StatCompact("Total", "${stats.totalCompletions}", "✅")
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Weekly Rings
            if (stats.last7Days.isNotEmpty()) {
                WeeklySuccessRings(stats.last7Days, color)
            }
        }
    }
}

@Composable
fun StatCompact(label: String, value: String, emoji: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(emoji, fontSize = 12.sp)
        Spacer(modifier = Modifier.width(4.dp))
        Column {
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp)
            Text(value, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitInteractionDialog(
    habit: Habit,
    onDismiss: () -> Unit,
    onConfirm: (Float, HabitMood?) -> Unit
) {
    var value by remember { mutableFloatStateOf(0f) }
    var selectedMood by remember { mutableStateOf<HabitMood?>(null) }
    val habitColor = Color(habit.iconColor)

    Dialog(
        onDismissRequest = onDismiss,
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .wrapContentHeight(),
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column {
                // Gradient Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = androidx.compose.ui.graphics.Brush.horizontalGradient(
                                colors = listOf(
                                    habitColor,
                                    habitColor.copy(alpha = 0.7f)
                                )
                            )
                        )
                        .padding(24.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = Color.White.copy(alpha = 0.2f),
                            modifier = Modifier.size(48.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(habit.icon, fontSize = 24.sp)
                            }
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                habit.title,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                "Log your progress for today",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // 1. Progress Input
                    if (habit.type == HabitType.QUANTITATIVE) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                "How much completed?",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold
                            )
                            OutlinedTextField(
                                value = if (value == 0f) "" else value.toString(),
                                onValueChange = { value = it.toFloatOrNull() ?: 0f },
                                label = { Text("Value (${habit.unit ?: "units"})") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Decimal
                                )
                            )
                        }
                    } else if (habit.type == HabitType.TIMER) {
                        Surface(
                            color = habitColor.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Timer, null, tint = habitColor)
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    "Log ${habit.targetValue.toInt()} mins session",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    } else {
                        Surface(
                            color = Color(0xFF4CAF50).copy(alpha = 0.1f),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF4CAF50))
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    "Marking as completed",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    // 2. Mood Selection
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            "How do you feel about this?",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            HabitMood.entries.forEach { mood ->
                                val isSelected = selectedMood == mood
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Surface(
                                        onClick = { selectedMood = mood },
                                        modifier = Modifier.size(50.dp),
                                        shape = CircleShape,
                                        color = if (isSelected) habitColor.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                                        border = if (isSelected) BorderStroke(2.dp, habitColor) else null
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(mood.emoji, fontSize = 24.sp)
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        mood.label,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (isSelected) habitColor else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // 3. Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f).height(52.dp),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text("Cancel")
                        }
                        Button(
                            onClick = {
                                val finalVal = if (habit.type == HabitType.YES_NO) 1f else value
                                onConfirm(finalVal, selectedMood)
                            },
                            modifier = Modifier.weight(1.5f).height(52.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = habitColor)
                        ) {
                            Text("Save Entry")
                        }
                    }
                }
            }
        }
    }
}
