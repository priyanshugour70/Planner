package com.lssgoo.planner.features.finance.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lssgoo.planner.data.model.Budget
import com.lssgoo.planner.ui.components.AppIcons

/**
 * Budget progress item component
 * Displays budget information with progress bar
 */
@Composable
fun BudgetProgressItem(
    budget: Budget,
    showDelete: Boolean = false,
    onDelete: (() -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val progress = (budget.spentAmount / budget.limitAmount).toFloat().coerceIn(0f, 1.2f)
    val color = if (progress > 1f) Color.Red else MaterialTheme.colorScheme.primary
    val remaining = (budget.limitAmount - budget.spentAmount).coerceAtLeast(0.0)
    
    var showDeleteConfirm by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(color.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = budget.category?.let { AppIcons.fromName(it.iconName) } ?: Icons.Default.Payments,
                            contentDescription = null,
                            tint = color,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            budget.category?.name?.lowercase()?.replaceFirstChar { it.uppercase() } ?: "Overall Budget",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            budget.period.name.lowercase().replaceFirstChar { it.uppercase() },
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                if (showDelete) {
                    IconButton(
                        onClick = { showDeleteConfirm = true },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            LinearProgressIndicator(
                progress = { progress.coerceAtMost(1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = color,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Spent: ₹${String.format("%.2f", budget.spentAmount)}",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    "Remaining: ₹${String.format("%.2f", remaining)}",
                    style = MaterialTheme.typography.labelMedium,
                    color = if (remaining < budget.limitAmount * 0.1) Color.Red else MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Medium
                )
            }

            // Budget Alert
            if (progress > 0.8f) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            if (progress > 1f) Color.Red.copy(alpha = 0.1f) else Color(0xFFFF9800).copy(alpha = 0.1f),
                            RoundedCornerShape(8.dp)
                        )
                        .padding(8.dp)
                ) {
                    Icon(
                        Icons.Default.Warning,
                        null,
                        modifier = Modifier.size(16.dp),
                        tint = if (progress > 1f) Color.Red else Color(0xFFFF9800)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        if (progress > 1f) "Budget Exceeded!" else "Approaching Limit",
                        style = MaterialTheme.typography.labelMedium,
                        color = if (progress > 1f) Color.Red else Color(0xFFFF9800),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
    
    // Delete Confirmation
    if (showDeleteConfirm && onDelete != null) {
        DeleteConfirmationDialog(
            title = "Delete Budget?",
            message = "Are you sure you want to delete this budget? This action cannot be undone.",
            onConfirm = {
                onDelete()
                showDeleteConfirm = false
            },
            onDismiss = { showDeleteConfirm = false }
        )
    }
}
