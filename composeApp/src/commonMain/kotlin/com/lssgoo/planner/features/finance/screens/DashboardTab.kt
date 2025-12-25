package com.lssgoo.planner.features.finance.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lssgoo.planner.data.model.*
import com.lssgoo.planner.features.finance.components.TransactionItem
import com.lssgoo.planner.features.finance.models.SavingsGoal
import com.lssgoo.planner.ui.components.AppIcons
import com.lssgoo.planner.ui.theme.FinanceColors

/**
 * Dashboard Tab - Financial overview
 */
@Composable
fun DashboardTab(
    stats: FinanceStats,
    savingsGoals: List<SavingsGoal> = emptyList(),
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Balance Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    "Current Balance",
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                    style = MaterialTheme.typography.labelMedium
                )
                Text(
                    "₹${String.format("%,.0f", stats.currentBalance)}",
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Row(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.ArrowDownward,
                                null,
                                modifier = Modifier.size(12.dp),
                                tint = FinanceColors.income
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                "Income",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                            )
                        }
                        Text(
                            "₹${String.format("%,.0f", stats.totalIncome)}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.ArrowUpward,
                                null,
                                modifier = Modifier.size(12.dp),
                                tint = FinanceColors.expense
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                "Expense",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                            )
                        }
                        Text(
                            "₹${String.format("%,.0f", stats.totalExpense)}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
        }

        // Upcoming Bills
        if (stats.upcomingRecurring.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    "Upcoming Bills",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(end = 16.dp)
                ) {
                    items(stats.upcomingRecurring) { bill: Transaction ->
                        Card(
                            modifier = Modifier.width(160.dp),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                            )
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Icon(
                                    imageVector = AppIcons.fromName(bill.category.iconName),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    bill.category.name.lowercase().replaceFirstChar { it.uppercase() },
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    "₹${String.format("%,.0f", bill.amount)}",
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Text(
                                    bill.recurringPeriod?.name ?: "Monthly",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        // Savings Goals - Real Data
        if (savingsGoals.isNotEmpty()) {
            val totalTarget = savingsGoals.sumOf { it.targetAmount }
            val totalSaved = savingsGoals.sumOf { it.currentAmount }
            val overallProgress = if (totalTarget > 0) (totalSaved / totalTarget * 100).toInt() else 0
            
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    "Savings Goals",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                if (savingsGoals.size == 1) {
                    // Single goal - show it directly
                    val goal = savingsGoals.first()
                    val progress = if (goal.targetAmount > 0) {
                        ((goal.currentAmount / goal.targetAmount) * 100).toInt()
                    } else 0
                    
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(androidx.compose.ui.graphics.Color(goal.color).copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Savings,
                                    contentDescription = null,
                                    tint = androidx.compose.ui.graphics.Color(goal.color),
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        goal.name,
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                    Text(
                                        "$progress%",
                                        color = androidx.compose.ui.graphics.Color(goal.color),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                LinearProgressIndicator(
                                    progress = { progress / 100f },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp)
                                        .height(6.dp)
                                        .clip(CircleShape),
                                    color = androidx.compose.ui.graphics.Color(goal.color),
                                    trackColor = androidx.compose.ui.graphics.Color(goal.color).copy(alpha = 0.1f)
                                )
                                Text(
                                    "Saved ₹${String.format("%,.0f", goal.currentAmount)} of ₹${String.format("%,.0f", goal.targetAmount)}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                } else {
                    // Multiple goals - show summary
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = AppIcons.Target,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        "${savingsGoals.size} Savings Goals",
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                    Text(
                                        "$overallProgress%",
                                        color = MaterialTheme.colorScheme.secondary,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                LinearProgressIndicator(
                                    progress = { overallProgress / 100f },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp)
                                        .height(6.dp)
                                        .clip(CircleShape),
                                    color = MaterialTheme.colorScheme.secondary,
                                    trackColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)
                                )
                                Text(
                                    "Saved ₹${String.format("%,.0f", totalSaved)} of ₹${String.format("%,.0f", totalTarget)}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        // Stats Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatsCompactCard(
                "Borrowed",
                "₹${String.format("%,.0f", stats.totalBorrowed)}",
                FinanceColors.expense,
                Modifier.weight(1f)
            )
            StatsCompactCard(
                "Lent",
                "₹${String.format("%,.0f", stats.totalLent)}",
                FinanceColors.income,
                Modifier.weight(1f)
            )
        }

        // Recent Transactions Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Recent Transactions",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            TextButton(onClick = { /* Could auto-switch tab */ }) {
                Text("See All", color = MaterialTheme.colorScheme.primary)
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            stats.recentTransactions.take(3).forEach { transaction ->
                TransactionItem(transaction)
            }
        }
        
        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun StatsCompactCard(
    label: String,
    value: String,
    color: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}
