package com.lssgoo.planner.features.finance.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.lssgoo.planner.features.finance.components.*
import com.lssgoo.planner.features.finance.models.SavingsGoal
import com.lssgoo.planner.util.KmpDateFormatter

/**
 * Savings Tab - Complete savings management
 * Following SRP - handles only savings UI
 */
@Composable
fun SavingsTab(
    savingsGoals: List<SavingsGoal>,
    onAddGoal: () -> Unit,
    onEditGoal: (SavingsGoal) -> Unit,
    onDeleteGoal: (String) -> Unit,
    onAddMoney: (SavingsGoal) -> Unit,
    modifier: Modifier = Modifier
) {
    if (savingsGoals.isEmpty()) {
        EmptySavingsState(onAddGoal = onAddGoal)
    } else {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Summary Card
            item {
                SavingsSummaryCard(savingsGoals = savingsGoals)
            }
            
            // Savings Goals
            items(savingsGoals.filter { !it.isDeleted }) { goal ->
                SavingsGoalCard(
                    goal = goal,
                    onEdit = { onEditGoal(goal) },
                    onDelete = { onDeleteGoal(goal.id) },
                    onAddMoney = { onAddMoney(goal) }
                )
            }
        }
    }
}

@Composable
private fun EmptySavingsState(onAddGoal: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.Savings,
            contentDescription = null,
            modifier = Modifier.size(120.dp),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            "No Savings Goals Yet",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Start building your financial future by creating your first savings goal",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onAddGoal,
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(Icons.Default.Add, null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Create Savings Goal")
        }
    }
}

@Composable
private fun SavingsSummaryCard(savingsGoals: List<SavingsGoal>) {
    val totalTarget = savingsGoals.sumOf { it.targetAmount }
    val totalSaved = savingsGoals.sumOf { it.currentAmount }
    val progress = if (totalTarget > 0) (totalSaved / totalTarget * 100).toInt() else 0
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "Total Savings",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                    Text(
                        "₹${String.format("%.2f", totalSaved)}",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                
                Box(contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        progress = { progress / 100f },
                        modifier = Modifier.size(60.dp),
                        strokeWidth = 6.dp,
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    )
                    Text(
                        "$progress%",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            LinearProgressIndicator(
                progress = { progress / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        "Target",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f)
                    )
                    Text(
                        "₹${String.format("%.2f", totalTarget)}",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        "Remaining",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f)
                    )
                    Text(
                        "₹${String.format("%.2f", totalTarget - totalSaved)}",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
private fun SavingsGoalCard(
    goal: SavingsGoal,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onAddMoney: () -> Unit
) {
    val progress = if (goal.targetAmount > 0) {
        ((goal.currentAmount / goal.targetAmount) * 100).toInt().coerceIn(0, 100)
    } else 0
    
    var showDeleteConfirm by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(goal.color).copy(alpha = 0.2f),
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            Icons.Default.Savings,
                            contentDescription = null,
                            tint = Color(goal.color),
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            goal.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            goal.category,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                Row {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, "Edit", tint = MaterialTheme.colorScheme.primary)
                    }
                    IconButton(onClick = { showDeleteConfirm = true }) {
                        Icon(Icons.Default.Delete, "Delete", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
            
            // Progress
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "₹${String.format("%.2f", goal.currentAmount)}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        "of ₹${String.format("%.2f", goal.targetAmount)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                LinearProgressIndicator(
                    progress = { progress / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = Color(goal.color),
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "$progress% Complete",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    goal.deadline?.let {
                        Text(
                            "Due: ${KmpDateFormatter.formatDate(it)}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            
            // Action Button
            Button(
                onClick = onAddMoney,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(goal.color)
                )
            ) {
                Icon(Icons.Default.Add, null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add Money")
            }
        }
    }
    
    if (showDeleteConfirm) {
        DeleteConfirmationDialog(
            title = "Delete Savings Goal?",
            message = "Are you sure you want to delete \"${goal.name}\"? This action cannot be undone.",
            onConfirm = {
                onDelete()
                showDeleteConfirm = false
            },
            onDismiss = { showDeleteConfirm = false }
        )
    }
}
