package com.lssgoo.planner.features.onboarding.steps

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lssgoo.planner.ui.components.AppIcons
import com.lssgoo.planner.ui.theme.GradientColors

@Composable
fun OnboardingWelcomeStep(firstName: String) {
    val infiniteTransition = rememberInfiniteTransition(label = "welcome_celebration")
    
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(15000, easing = LinearEasing), RepeatMode.Restart),
        label = "rotation"
    )

    var startAnim by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        startAnim = true
    }

    val animValue by animateFloatAsState(
        targetValue = if (startAnim) 1f else 0f,
        animationSpec = tween(1000, easing = EaseOutBack)
    )

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        // Confetti Icons
        if (animValue > 0.5f) {
            ConfettiIcons()
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(contentAlignment = Alignment.Center) {
                // Outer rotating glow
                Box(
                    modifier = Modifier
                        .size(240.dp)
                        .graphicsLayer { rotationZ = rotation }
                        .blur(40.dp)
                        .background(
                            Brush.sweepGradient(
                                listOf(Color(0xFF10B981), Color(0xFF3B82F6), Color(0xFFEC4899), Color(0xFF10B981))
                            ),
                            shape = CircleShape
                        )
                )

                Surface(
                    modifier = Modifier
                        .size(160.dp)
                        .scale(animValue)
                        .shadow(elevation = 32.dp, shape = CircleShape, spotColor = Color(0xFF10B981).copy(alpha = 0.5f)),
                    shape = CircleShape,
                    color = Color.White,
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = AppIcons.Celebration,
                            contentDescription = null,
                            modifier = Modifier.size(80.dp),
                            tint = Color(0xFF10B981)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            Text(
                text = "Welcome aboard, $firstName!",
                style = MaterialTheme.typography.displaySmall.copy(
                    brush = Brush.linearGradient(
                        listOf(Color(0xFF10B981), Color(0xFF3B82F6))
                    )
                ),
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier.graphicsLayer {
                    translationY = (1f - animValue) * 50f
                    alpha = animValue
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Your personalized planner is ready. Let's make 2026 your most productive and fulfilling year yet.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp).graphicsLayer {
                    translationY = (1f - animValue) * 70f
                    alpha = animValue
                }
            )

            Spacer(modifier = Modifier.height(60.dp))
            
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        translationY = (1f - animValue) * 100f
                        alpha = animValue
                    },
                shape = RoundedCornerShape(32.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
            ) {
                Row(
                    modifier = Modifier.padding(24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.size(40.dp).background(MaterialTheme.colorScheme.primary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = AppIcons.Notification,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Tip: You can change all your settings anytime in your profile section.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun ConfettiIcons() {
    val infiniteTransition = rememberInfiniteTransition()
    
    val icons = listOf(AppIcons.Target, AppIcons.Habit, AppIcons.Rocket, AppIcons.Star)
    val colors = listOf(Color(0xFF10B981), Color(0xFF3B82F6), Color(0xFFEC4899), Color(0xFFF59E0B))
    
    Box(modifier = Modifier.fillMaxSize()) {
        repeat(12) { i ->
            val angle = (360f / 12f) * i
            val radius by infiniteTransition.animateFloat(
                initialValue = 100f, targetValue = 600f,
                animationSpec = infiniteRepeatable(tween(2000 + (i * 100), easing = EaseOutCubic), RepeatMode.Restart)
            )
            val alpha by infiniteTransition.animateFloat(
                initialValue = 1f, targetValue = 0f,
                animationSpec = infiniteRepeatable(tween(2000 + (i * 100), easing = EaseOutCubic), RepeatMode.Restart)
            )
            
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(
                        x = (radius * kotlin.math.cos(Math.toRadians(angle.toDouble()))).dp,
                        y = (radius * kotlin.math.sin(Math.toRadians(angle.toDouble()))).dp
                    )
                    .graphicsLayer { this.alpha = alpha }
            ) {
                Icon(
                    imageVector = icons[i % icons.size],
                    contentDescription = null,
                    tint = colors[i % colors.size].copy(alpha = 0.6f),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
