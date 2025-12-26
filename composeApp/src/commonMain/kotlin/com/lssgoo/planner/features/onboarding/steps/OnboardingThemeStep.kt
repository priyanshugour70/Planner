package com.lssgoo.planner.features.onboarding.steps

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import com.lssgoo.planner.features.settings.models.ThemeMode
import com.lssgoo.planner.ui.theme.ThemePreviewColors

@Composable
fun OnboardingThemeStep(
    selectedTheme: ThemeMode,
    onThemeSelected: (ThemeMode) -> Unit
) {
    val animState = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        animState.animateTo(1f, animationSpec = tween(800, easing = EaseOutBack))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Pick Your Vibe",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center,
            modifier = Modifier.graphicsLayer {
                translationY = (1f - animState.value) * 50f
                alpha = animState.value
            }
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Text(
            text = "Choose a theme that matches your style. You can always change this later in settings.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 8.dp).graphicsLayer {
                translationY = (1f - animState.value) * 70f
                alpha = animState.value
            }
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val themeEntries = ThemeMode.entries
            items(themeEntries.size) { index ->
                val theme = themeEntries[index]
                
                // Staggered animation for each card
                var startAnim by remember { mutableStateOf(false) }
                LaunchedEffect(Unit) {
                    delay(index.toLong() * 50L)
                    startAnim = true
                }

                val cardAlpha by animateFloatAsState(
                    targetValue = if (startAnim) 1f else 0f,
                    animationSpec = tween(500, easing = EaseOutBack)
                )
                val cardOffsetY by animateFloatAsState(
                    targetValue = if (startAnim) 0f else 100f,
                    animationSpec = tween(500, easing = EaseOutBack)
                )
                val cardScale by animateFloatAsState(
                    targetValue = if (startAnim) 1f else 0.8f,
                    animationSpec = tween(500, easing = EaseOutBack)
                )

                Box(modifier = Modifier.graphicsLayer {
                    translationY = cardOffsetY
                    alpha = cardAlpha
                    scaleX = cardScale
                    scaleY = cardScale
                }) {
                    ThemeCard(
                        theme = theme,
                        isSelected = selectedTheme == theme,
                        onClick = { onThemeSelected(theme) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ThemeCard(
    theme: ThemeMode,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val previewColor = when (theme) {
        ThemeMode.LIGHT -> Color.White
        ThemeMode.DARK -> Color.Black
        ThemeMode.SYSTEM -> MaterialTheme.colorScheme.outline
        ThemeMode.OCEAN -> ThemePreviewColors.ocean
        ThemeMode.SUNSET -> ThemePreviewColors.sunset
        ThemeMode.FOREST -> ThemePreviewColors.forest
        ThemeMode.MIDNIGHT -> ThemePreviewColors.midnight
        ThemeMode.ROSE_GOLD -> ThemePreviewColors.roseGold
        ThemeMode.NORD -> ThemePreviewColors.nord
        ThemeMode.SOLARIZED -> ThemePreviewColors.solarized
        ThemeMode.LAVENDER -> ThemePreviewColors.lavender
        ThemeMode.MOCHA -> ThemePreviewColors.mocha
    }

    val displayName = when (theme) {
        ThemeMode.LIGHT -> "Classic Light"
        ThemeMode.DARK -> "Classic Dark"
        ThemeMode.SYSTEM -> "System"
        ThemeMode.OCEAN -> "Deep Ocean"
        ThemeMode.SUNSET -> "Sunset Glow"
        ThemeMode.FOREST -> "Forest Green"
        ThemeMode.MIDNIGHT -> "Midnight"
        ThemeMode.ROSE_GOLD -> "Rose Gold"
        ThemeMode.NORD -> "Nord Arctic"
        ThemeMode.SOLARIZED -> "Solarized"
        ThemeMode.LAVENDER -> "Lavender"
        ThemeMode.MOCHA -> "Mocha"
    }

    Surface(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp),
        shape = RoundedCornerShape(24.dp),
        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(previewColor)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = if (theme == ThemeMode.LIGHT) Color.Black else Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(10.dp))
            
            Text(
                text = displayName,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}
