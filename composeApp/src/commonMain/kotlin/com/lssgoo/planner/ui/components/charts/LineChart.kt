package com.lssgoo.planner.ui.components.charts

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.unit.dp

@Composable
fun SmoothLineChart(
    data: List<ChartDataPoint>,
    modifier: Modifier = Modifier,
    lineColor: Color = MaterialTheme.colorScheme.primary,
    fillColor: Color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), // Gradient would be better
    showDots: Boolean = true
) {
    if (data.isEmpty()) return

    var animationPlayed by remember { mutableStateOf(false) }
    val progress = animateFloatAsState(
        targetValue = if (animationPlayed) 1f else 0f,
        animationSpec = tween(1500, easing = FastOutSlowInEasing)
    )
    
    LaunchedEffect(key1 = true) {
        animationPlayed = true
    }

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val spacing = width / (data.size - 1).coerceAtLeast(1)
        val maxDataValue = data.maxOfOrNull { it.value } ?: 1f
        val minDataValue = 0f // Baseline at 0 usually

        val points = data.mapIndexed { index, point ->
            val x = index * spacing
            val normalizedValue = (point.value / maxDataValue)
            val y = height - (normalizedValue * height * progress.value) // Animate height
            Offset(x, y)
        }

        if (points.isEmpty()) return@Canvas

        val path = Path()
        path.moveTo(points.first().x, points.first().y)

        for (i in 0 until points.size - 1) {
            val p1 = points[i]
            val p2 = points[i + 1]
            
            // Cubic Bezier for smooth curves through points
            // Control points technique from standard charting libs
            val cx1 = (p1.x + p2.x) / 2
            val cy1 = p1.y
            val cx2 = (p1.x + p2.x) / 2
            val cy2 = p2.y
            
            path.cubicTo(cx1, cy1, cx2, cy2, p2.x, p2.y)
        }

        // Draw Line
        drawPath(
            path = path,
            color = lineColor,
            style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        )
        
        // Draw Fill
        val fillPath = Path()
        fillPath.addPath(path)
        fillPath.lineTo(points.last().x, height)
        fillPath.lineTo(points.first().x, height)
        fillPath.close()
        
        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(
                    lineColor.copy(alpha = 0.4f),
                    lineColor.copy(alpha = 0.0f)
                ),
                startY = 0f,
                endY = height
            )
        )

        // Draw Dots
        if (showDots) {
            points.forEach { point ->
                drawCircle(
                    color = Color.White,
                    radius = 5.dp.toPx(),
                    center = point,
                    style = Fill
                )
                drawCircle(
                    color = lineColor,
                    radius = 5.dp.toPx(),
                    center = point,
                    style = Stroke(width = 2.dp.toPx())
                )
            }
        }
    }
}
