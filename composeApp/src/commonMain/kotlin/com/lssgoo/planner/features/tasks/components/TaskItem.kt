package com.lssgoo.planner.features.tasks.components

import androidx.compose.foundation.background
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.lssgoo.planner.features.tasks.models.Task
import com.lssgoo.planner.features.tasks.models.TaskPriority
import com.lssgoo.planner.ui.components.AppIcons
import com.lssgoo.planner.ui.theme.completedColor

@Composable
fun TaskItem(
    task: Task,
    onToggle: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val priorityColor = Color(task.priority.color)
    val completedColor = completedColor
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (task.isCompleted) 
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Checkbox with animation
            IconButton(onClick = onToggle) {
                Icon(
                    imageVector = if (task.isCompleted) AppIcons.CheckCircle else AppIcons.RadioButtonUnchecked,
                    contentDescription = "Toggle task",
                    tint = if (task.isCompleted) completedColor else priorityColor
                )
            }
            
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
            ) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    textDecoration = if (task.isCompleted) 
                        androidx.compose.ui.text.style.TextDecoration.LineThrough 
                    else null,
                    color = if (task.isCompleted) 
                        MaterialTheme.colorScheme.onSurfaceVariant 
                    else MaterialTheme.colorScheme.onSurface
                )
                
                if (task.description.isNotEmpty()) {
                    Text(
                        text = task.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            
            // Priority indicator
            if (!task.isCompleted && task.priority == TaskPriority.HIGH) {
                Icon(
                    imageVector = AppIcons.PriorityHigh,
                    contentDescription = "High Priority",
                    tint = priorityColor,
                    modifier = Modifier.size(16.dp)
                )
            } else if (!task.isCompleted) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(priorityColor)
                )
            }
        }
    }
}
