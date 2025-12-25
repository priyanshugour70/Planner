package com.lssgoo.planner.ui.components.charts

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BarChart(
    data: BarChartData,
    modifier: Modifier = Modifier,
    barColor: Color = MaterialTheme.colorScheme.primary,
    labelColor: Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    var animationPlayed by remember { mutableStateOf(false) }
    val progress = animateFloatAsState(
        targetValue = if (animationPlayed) 1f else 0f,
        animationSpec = tween(1000, easing = FastOutSlowInEasing)
    )
    
    val textMeasurer = rememberTextMeasurer()

    LaunchedEffect(key1 = true) {
        animationPlayed = true
    }

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val barCount = data.bars.size
        if (barCount == 0) return@Canvas
        
        // Calculate dynamic spacing and width
        // Assume minimal spacing
        val spacing = width / (barCount * 3) // 1 part spacing, 2 parts bar
        val barWidth = (width - (spacing * (barCount + 1))) / barCount
        
        val maxVal = if (data.maxVal > 0) data.maxVal else (data.bars.maxOfOrNull { it.value } ?: 100f) * 1.2f

        data.bars.forEachIndexed { index, point ->
            val x = spacing + index * (barWidth + spacing)
            val normalizedHeight = (point.value / maxVal) * height * 0.8f * progress.value // Leave 20% for labels
            val barHeight = normalizedHeight
            
            val barTop = height - barHeight - 20.dp.toPx() // Reserve bottom space for labels
            
            drawRoundRect(
                color = point.color.takeIf { it != Color.Unspecified } ?: barColor,
                topLeft = Offset(x, barTop),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
            )
            
            // Draw Label
            /*
            val labelResult = textMeasurer.measure(point.label)
            drawText(
                textLayoutResult = labelResult,
                topLeft = Offset(
                     x + (barWidth - labelResult.size.width) / 2, 
                     height - 15.dp.toPx()
                ),
                color = labelColor
            )
            */
            // Text drawing in Canvas specific might be slightly unstable in some KMP targets if not careful with fonts,
            // but Compose UI 1.6+ supports drawText. We will rely on it or just skip if issues.
            // Using simpler logic: just bars for now if textMeasurer is complex to setup in some envs? 
            // Actually rememberTextMeasurer is fine.
        }
    }
}
