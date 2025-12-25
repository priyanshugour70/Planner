package com.lssgoo.planner.ui.components.charts

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class ChartDataPoint(
    val label: String,
    val value: Float,
    val color: Color = Color.Unspecified
)

data class BarChartData(
    val bars: List<ChartDataPoint>,
    val maxVal: Float = -1f // -1 auto calculate
)

data class LineChartData(
    val points: List<ChartDataPoint>,
    val color: Color = Color.Blue
)

data class PieChartData(
    val slices: List<ChartDataPoint>
)

data class DonutChartData(
    val value: Float,
    val maxValue: Float = 100f,
    val color: Color = Color.Blue,
    val label: String = "",
    val subLabel: String = ""
)
