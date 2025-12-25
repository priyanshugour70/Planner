package com.lssgoo.planner.features.goals.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lssgoo.planner.features.goals.models.Milestone

@Composable
fun MilestoneProgressChart(
    milestones: List<Milestone>,
    goalColor: Color,
    modifier: Modifier = Modifier
) {
    if (milestones.isEmpty()) return

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Milestone Visualization",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(24.dp))
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val width = size.width
                    val height = size.height
                    val centerY = height / 2
                    
                    // Draw base line
                    drawLine(
                        color = Color.LightGray.copy(alpha = 0.5f),
                        start = Offset(20f, centerY),
                        end = Offset(width - 20f, centerY),
                        strokeWidth = 4f,
                        cap = StrokeCap.Round
                    )
                    
                    // Draw progress line
                    val completedCount = milestones.count { it.isCompleted }
                    if (completedCount > 0) {
                        val progressWidth = (width - 40f) * (completedCount.toFloat() / milestones.size)
                        drawLine(
                            color = goalColor,
                            start = Offset(20f, centerY),
                            end = Offset(20f + progressWidth, centerY),
                            strokeWidth = 4f,
                            cap = StrokeCap.Round
                        )
                    }
                    
                    // Draw dots
                    val step = (width - 40f) / (milestones.size - 1).coerceAtLeast(1)
                    
                    milestones.forEachIndexed { index, m ->
                        val x = 20f + (step * index)
                        val color = if (m.isCompleted) goalColor else Color.LightGray
                        val radius = if (m.isCompleted) 12f else 8f
                        
                        drawCircle(
                            color = color,
                            radius = radius,
                            center = Offset(x, centerY)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Start",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    "Finish",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
