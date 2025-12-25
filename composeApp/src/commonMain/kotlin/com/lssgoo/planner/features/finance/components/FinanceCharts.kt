package com.lssgoo.planner.features.finance.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lssgoo.planner.features.finance.models.TransactionCategory
import com.lssgoo.planner.ui.components.charts.*
import com.lssgoo.planner.ui.theme.FinanceColors

@Composable
fun SpendingPieChart(
    data: Map<TransactionCategory, Double>,
    modifier: Modifier = Modifier
) {
    val total = data.values.sum()
    if (total == 0.0) return

    val chartData = remember(data) {
        val pieData = data.entries.map { (cat, value) ->
            ChartDataPoint(
                label = cat.name,
                value = value.toFloat(),
                color = getCategoryColor(cat)
            )
        }
        PieChartData(pieData)
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Box(modifier = Modifier.size(160.dp), contentAlignment = Alignment.Center) {
            PieChart(
                data = chartData,
                modifier = Modifier.size(140.dp),
                innerRadiusRatio = 0.6f // Donut style
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Total", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("₹${total.toInt()}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            data.entries.sortedByDescending { it.value }.take(5).forEach { (cat, value) ->
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(getCategoryColor(cat)))
                    Text(cat.name.lowercase().replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }, fontSize = 12.sp, modifier = Modifier.weight(1f))
                    Text("${(value / total * 100).toInt()}%", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun ExpenseLineChart(
    data: Map<Long, Double>,
    modifier: Modifier = Modifier
) {
    if (data.isEmpty()) return
    
    val chartData = remember(data) {
        data.entries.sortedBy { it.key }.map { (date, value) ->
            ChartDataPoint(
                label = date.toString(),
                value = value.toFloat()
            )
        }
    }

    Box(modifier = modifier.fillMaxWidth().height(180.dp).padding(top = 16.dp)) {
        SmoothLineChart(
            data = chartData,
            lineColor = FinanceColors.expense,
            modifier = Modifier.fillMaxSize(),
            showDots = true
        )
    }
}

private fun getCategoryColor(category: TransactionCategory): Color {
    return when (category) {
        TransactionCategory.FOOD -> Color(0xFFFF9800)
        TransactionCategory.TRANSPORT -> Color(0xFF2196F3)
        TransactionCategory.SHOPPING -> Color(0xFFE91E63)
        TransactionCategory.ENTERTAINMENT -> Color(0xFF9C27B0)
        TransactionCategory.HEALTH -> Color(0xFF4CAF50)
        TransactionCategory.EDUCATION -> Color(0xFF00BCD4)
        TransactionCategory.SALARY -> Color(0xFF4CAF50)
        TransactionCategory.INVESTMENT -> Color(0xFFFFC107)
        TransactionCategory.BILL -> Color(0xFF607D8B)
        TransactionCategory.RENT -> Color(0xFF795548)
        TransactionCategory.GIFT -> Color(0xFFFF5722)
        TransactionCategory.OTHER -> Color(0xFF9E9E9E)
        TransactionCategory.DEBT_REPAYMENT -> Color(0xFF3F51B5)
    }
}
