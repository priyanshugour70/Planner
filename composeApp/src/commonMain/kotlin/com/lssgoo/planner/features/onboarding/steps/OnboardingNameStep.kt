package com.lssgoo.planner.features.onboarding.steps

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun OnboardingNameStep(
    firstName: String,
    lastName: String,
    onFirstNameChange: (String) -> Unit,
    onLastNameChange: (String) -> Unit,
    focusManager: FocusManager
) {
    var startAnim by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        startAnim = true
    }

    val animValue by animateFloatAsState(
        targetValue = if (startAnim) 1f else 0f,
        animationSpec = tween(800, easing = EaseOutBack)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Floating Avatar Circle
        Box(
            modifier = Modifier
                .size(100.dp)
                .graphicsLayer {
                    alpha = animValue
                    scaleX = animValue
                    scaleY = animValue
                }
                .shadow(16.dp, CircleShape)
                .background(
                    Brush.linearGradient(listOf(Color(0xFF6366F1), Color(0xFFA855F7))),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Person,
                contentDescription = null,
                modifier = Modifier.size(50.dp),
                tint = Color.White
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "Nice to meet you!",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center,
            modifier = Modifier.graphicsLayer {
                translationY = (1f - animValue) * 100f
                alpha = animValue
            }
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Text(
            text = "Let's start with your name",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.graphicsLayer {
                translationY = (1f - animValue) * 120f
                alpha = animValue
            }
        )
        
        Spacer(modifier = Modifier.height(48.dp))
        
        OutlinedTextField(
            value = firstName,
            onValueChange = onFirstNameChange,
            label = { Text("First Name *") },
            placeholder = { Text("Enter your first name") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer {
                    translationY = (1f - animValue) * 150f
                    alpha = animValue
                },
            shape = RoundedCornerShape(20.dp),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            leadingIcon = {
                Icon(Icons.Outlined.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            }
        )
        
        Spacer(modifier = Modifier.height(20.dp))
        
        OutlinedTextField(
            value = lastName,
            onValueChange = onLastNameChange,
            label = { Text("Last Name") },
            placeholder = { Text("Enter your last name (optional)") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer {
                    translationY = (1f - animValue) * 180f
                    alpha = animValue
                },
            shape = RoundedCornerShape(20.dp),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { focusManager.clearFocus() }
            ),
            leadingIcon = {
                Icon(Icons.Outlined.Badge, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            }
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Text(
            text = "* Required for personlized experience",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.error.copy(alpha = 0.8f),
            modifier = Modifier.graphicsLayer { alpha = animValue }
        )
    }
}
