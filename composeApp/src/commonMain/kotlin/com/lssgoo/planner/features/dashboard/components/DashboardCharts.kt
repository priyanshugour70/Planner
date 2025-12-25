package com.lssgoo.planner.features.dashboard.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lssgoo.planner.data.model.Goal
import com.lssgoo.planner.features.habits.models.Habit
import com.lssgoo.planner.data.model.FinanceStats
import com.lssgoo.planner.ui.components.charts.*
import com.lssgoo.planner.ui.theme.FinanceColors
import com.lssgoo.planner.ui.theme.GoalColors

@Composable
fun DashboardGoalPieChart(
    goals: List<Goal>,
    modifier: Modifier = Modifier
) {
    // Group goals by completion
    val completed = goals.count { it.milestones.all { m -> m.isCompleted } && it.milestones.isNotEmpty() }
    val inProgress = goals.count { it.milestones.any { m -> m.isCompleted } && !it.milestones.all { m -> m.isCompleted } }
    val notStarted = goals.count { it.milestones.none { m -> m.isCompleted } }

    val completedColor = GoalColors.health
    val inProgressColor = GoalColors.career
    val notStartedColor = MaterialTheme.colorScheme.surfaceVariant

    val data = remember(goals, completedColor, inProgressColor, notStartedColor) {
        PieChartData(
            listOf(
                ChartDataPoint("Completed", completed.toFloat(), completedColor),
                ChartDataPoint("In Progress", inProgress.toFloat(), inProgressColor),
                ChartDataPoint("Not Started", notStarted.toFloat(), notStartedColor)
            )
        )
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("Goal Status", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(20.dp))
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) {
                PieChart(
                    data = data,
                    modifier = Modifier.size(200.dp),
                    innerRadiusRatio = 0.6f // Donut style
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${goals.size}",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text("Total Goals", style = MaterialTheme.typography.bodySmall)
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                LegendItem("Done", completed.toString(), GoalColors.health)
                LegendItem("Active", inProgress.toString(), GoalColors.career)
                LegendItem("New", notStarted.toString(), MaterialTheme.colorScheme.surfaceVariant)
            }
        }
    }
}

@Composable
fun DashboardFinanceLineChart(
    stats: FinanceStats,
    modifier: Modifier = Modifier
) {
    // Mock daily spending data for the graph
    val data = remember(stats) {
        listOf(
            ChartDataPoint("Mon", (stats.totalExpense * 0.1).toFloat()),
            ChartDataPoint("Tue", (stats.totalExpense * 0.4).toFloat()),
            ChartDataPoint("Wed", (stats.totalExpense * 0.2).toFloat()),
            ChartDataPoint("Thu", (stats.totalExpense * 0.6).toFloat()),
            ChartDataPoint("Fri", (stats.totalExpense * 0.3).toFloat()),
            ChartDataPoint("Sat", (stats.totalExpense * 0.8).toFloat()),
            ChartDataPoint("Sun", (stats.totalExpense * 0.5).toFloat())
        )
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                 Text("Expense Trend", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                 Text("This Week", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
            }
           
            Spacer(modifier = Modifier.height(20.dp))
            
            SmoothLineChart(
                data = data,
                modifier = Modifier.fillMaxWidth().height(150.dp),
                lineColor = FinanceColors.expense,
                showDots = true
            )
        }
    }
}

@Composable
fun DashboardHabitBarChart(
    habits: List<Habit>,
    modifier: Modifier = Modifier
) {
    // Mock weekly completion
    val data = remember(habits) {
        BarChartData(
            bars = listOf(
                ChartDataPoint("M", 80f, GoalColors.lifestyle),
                ChartDataPoint("T", 60f, GoalColors.lifestyle),
                ChartDataPoint("W", 90f, GoalColors.lifestyle),
                ChartDataPoint("T", 70f, GoalColors.lifestyle),
                ChartDataPoint("F", 50f, GoalColors.lifestyle),
                ChartDataPoint("S", 85f, GoalColors.lifestyle),
                ChartDataPoint("S", 40f, GoalColors.lifestyle),
            ),
            maxVal = 100f
        )
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("Habit Consistency", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(20.dp))
            BarChart(
                data = data,
                modifier = Modifier.fillMaxWidth().height(150.dp),
                barColor = GoalColors.lifestyle
            )
        }
    }
}
