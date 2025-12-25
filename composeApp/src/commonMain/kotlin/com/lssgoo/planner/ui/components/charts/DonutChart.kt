package com.lssgoo.planner.ui.components.charts

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun AnimatedCircleChart(
    value: Float,
    maxValue: Float = 100f,
    color: Color = MaterialTheme.colorScheme.primary,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
    strokeWidth: Dp = 12.dp,
    modifier: Modifier = Modifier,
    useSemiCircle: Boolean = false,
    label: String? = null,
    subLabel: String? = null
) {
    var animationPlayed by remember { mutableStateOf(false) }
    val currentPercentage = animateFloatAsState(
        targetValue = if (animationPlayed) (value / maxValue).coerceIn(0f, 1f) else 0f,
        animationSpec = tween(
            durationMillis = 1000,
            delayMillis = 100,
            easing = FastOutSlowInEasing
        )
    )

    LaunchedEffect(key1 = true) {
        animationPlayed = true
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidthPx = strokeWidth.toPx()
            val arcSize = size.minDimension - strokeWidthPx
            val topLeft = Offset(
                (size.width - arcSize) / 2,
                (size.height - arcSize) / 2
            )
            
            val sweepAngle = if (useSemiCircle) 180f else 360f
            val startAngle = if (useSemiCircle) 180f else -90f

            // Background Circle
            drawArc(
                color = backgroundColor,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round),
                size = Size(arcSize, arcSize),
                topLeft = topLeft
            )

            // Foreground Circle
            drawArc(
                color = color,
                startAngle = startAngle,
                sweepAngle = sweepAngle * currentPercentage.value,
                useCenter = false,
                style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round),
                size = Size(arcSize, arcSize),
                topLeft = topLeft
            )
            
            // Optional: Draw a dot at the end of the arc
            if (currentPercentage.value > 0) {
                 val angleRad = (startAngle + sweepAngle * currentPercentage.value) * (Math.PI / 180f)
                 val radius = arcSize / 2
                 val cx = size.width / 2 + radius * kotlin.math.cos(angleRad).toFloat()
                 val cy = size.height / 2 + radius * kotlin.math.sin(angleRad).toFloat()
                 
                 drawCircle(
                     color = Color.White,
                     radius = strokeWidthPx * 0.4f,
                     center = Offset(cx, cy)
                 )
            }
        }
        
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = if (useSemiCircle) 40.dp else 0.dp)
        ) {
            if (label != null) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            if (subLabel != null) {
                Text(
                    text = subLabel,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
