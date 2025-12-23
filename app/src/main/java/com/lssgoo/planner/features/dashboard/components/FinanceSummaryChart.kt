package com.lssgoo.planner.features.dashboard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lssgoo.planner.data.model.FinanceStats
import com.lssgoo.planner.ui.theme.FinanceColors

@Composable
fun FinanceSummaryChart(stats: FinanceStats, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Radial Chart
            Box(contentAlignment = Alignment.Center) {
                val total = (stats.totalIncome + stats.totalExpense).coerceAtLeast(1.0)
                val incomeRatio = (stats.totalIncome / total).toFloat()
                
                CircularProgressIndicator(
                    progress = { 1f },
                    modifier = Modifier.size(100.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    strokeWidth = 12.dp,
                    strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                )
                CircularProgressIndicator(
                    progress = { incomeRatio },
                    modifier = Modifier.size(100.dp),
                    color = FinanceColors.income,
                    strokeWidth = 12.dp,
                    strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                )
                
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Flow", style = MaterialTheme.typography.labelSmall)
                    Text(
                        "${(incomeRatio * 100).toInt()}%",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(28.dp))
            
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                LegendItem(label = "Income", value = "₹${stats.totalIncome.toInt()}", color = FinanceColors.income)
                LegendItem(label = "Expense", value = "₹${stats.totalExpense.toInt()}", color = FinanceColors.expense)
                LegendItem(label = "Balance", value = "₹${stats.currentBalance.toInt()}", color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

