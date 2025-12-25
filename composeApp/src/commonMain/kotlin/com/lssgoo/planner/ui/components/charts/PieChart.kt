package com.lssgoo.planner.ui.components.charts

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun PieChart(
    data: PieChartData,
    modifier: Modifier = Modifier,
    innerRadiusRatio: Float = 0f // 0f for Pie, >0f for Donut (e.g. 0.5f)
) {
    if (data.slices.isEmpty()) return

    var animationPlayed by remember { mutableStateOf(false) }
    val progress = animateFloatAsState(
        targetValue = if (animationPlayed) 1f else 0f,
        animationSpec = tween(1000, easing = FastOutSlowInEasing)
    )

    LaunchedEffect(key1 = true) {
        animationPlayed = true
    }

    val total = data.slices.sumOf { it.value.toDouble() }.toFloat()

    Canvas(modifier = modifier) {
        val diameter = size.minDimension
        val radius = diameter / 2f
        val innerRadius = radius * innerRadiusRatio
        val center = Offset(size.width / 2f, size.height / 2f)
        
        var startAngle = -90f

        data.slices.forEach { slice ->
            val sweepAngle = (slice.value / total) * 360f * progress.value
            
            drawArc(
                color = slice.color,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = true, // For Pie we use center
                topLeft = Offset(center.x - radius, center.y - radius),
                size = androidx.compose.ui.geometry.Size(diameter, diameter)
            )
            
            startAngle += sweepAngle
        }
        
        // If it's a donut, punch a hole
         if (innerRadiusRatio > 0f) {
             drawCircle(
                 color = Color.White, // Assume white background, passed background color would be better
                 radius = innerRadius,
                 center = center
             )
         }
    }
}
