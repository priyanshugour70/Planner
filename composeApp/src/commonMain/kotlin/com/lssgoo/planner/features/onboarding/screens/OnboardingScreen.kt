package com.lssgoo.planner.features.onboarding.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lssgoo.planner.data.model.Gender
import com.lssgoo.planner.features.settings.models.ThemeMode
import com.lssgoo.planner.features.settings.models.UserProfile
import com.lssgoo.planner.features.onboarding.steps.*
import com.lssgoo.planner.ui.viewmodel.PlannerViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(viewModel: PlannerViewModel) {
    val scope = rememberCoroutineScope()
    val haptic = LocalHapticFeedback.current
    val focusManager = LocalFocusManager.current
    
    // User data state
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var dateOfBirth by remember { mutableStateOf<Long?>(null) }
    var selectedGender by remember { mutableStateOf(Gender.PREFER_NOT_TO_SAY) }
    var occupation by remember { mutableStateOf("") }
    val initialTheme = viewModel.settings.collectAsState().value.themeMode
    var selectedTheme by remember { mutableStateOf(initialTheme) }
    
    val pagerState = rememberPagerState(pageCount = { 6 })
    
    // Validation
    val isNameValid = firstName.isNotBlank()
    val isEmailValid = email.isNotBlank() && email.contains("@") && email.contains(".")
    val canGoForward = when(pagerState.currentPage) {
        1 -> isNameValid
        2 -> isEmailValid
        else -> true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Shared Animated Background (from previous version as requested or simplified)
        AnimatedBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.systemBars),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Bar: Skip button & Progress
            OnboardingTopBar(
                pagerState = pagerState,
                onSkip = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    // Skip logic: Jump to finish or home? User said "discourage it".
                    // For now, skip just goes to the last page if allowed.
                    // Skip logic: Jump to last page
                    if (pagerState.currentPage != 1 && pagerState.currentPage != 2) {
                        scope.launch { pagerState.animateScrollToPage(5) }
                    }
                }
            )
            
            // Steps with 3D Cube Rotation Effect
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f),
                userScrollEnabled = false,
                contentPadding = PaddingValues(horizontal = 32.dp),
                pageSpacing = 0.dp
            ) { pageIndex ->
                val pageOffset = (
                    (pagerState.currentPage - pageIndex) + pagerState.currentPageOffsetFraction
                )
                
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            // 3D Cube transformation
                            val lerpOffset = pageOffset.coerceIn(-1f, 1f)
                            cameraDistance = 12f * density
                            
                            // Rotation
                            rotationY = lerpOffset * 90f
                            
                            // Pivot point for cube effect
                            transformOrigin = TransformOrigin(
                                pivotFractionX = if (lerpOffset < 0f) 0f else 1f,
                                pivotFractionY = 0.5f
                            )
                            
                            // Scale and Alpha
                            val scale = 0.85f + (1f - 0.85f) * (1f - pageOffset.absoluteValue.coerceIn(0f, 1f))
                            scaleX = scale
                            scaleY = scale
                            alpha = 1f - pageOffset.absoluteValue.coerceIn(0f, 0.7f)
                        }
                ) {
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 20.dp, vertical = 20.dp),
                        shape = RoundedCornerShape(32.dp),
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp, 
                            Brush.linearGradient(
                                listOf(Color.White.copy(alpha = 0.4f), Color.Transparent)
                            )
                        ),
                        tonalElevation = 4.dp,
                        shadowElevation = 8.dp
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            when (pageIndex) {
                                0 -> OnboardingIntroStep()
                                1 -> OnboardingNameStep(
                                    firstName = firstName,
                                    lastName = lastName,
                                    onFirstNameChange = { firstName = it },
                                    onLastNameChange = { lastName = it },
                                    focusManager = focusManager
                                )
                                2 -> OnboardingEmailStep(
                                    email = email,
                                    onEmailChange = { email = it },
                                    focusManager = focusManager
                                )
                                3 -> OnboardingThemeStep(
                                    selectedTheme = selectedTheme,
                                    onThemeSelected = { 
                                        selectedTheme = it 
                                        viewModel.updateSettings(viewModel.settings.value.copy(themeMode = it))
                                    }
                                )
                                4 -> OnboardingDetailsStep(
                                    dateOfBirth = dateOfBirth,
                                    selectedGender = selectedGender,
                                    occupation = occupation,
                                    onDateSelected = { dateOfBirth = it },
                                    onGenderSelected = { selectedGender = it },
                                    onOccupationChange = { occupation = it },
                                    focusManager = focusManager
                                )
                                5 -> OnboardingWelcomeStep(firstName = firstName)
                            }
                        }
                    }
                }
            }

            // Bottom Navigation - Swipe Inspired
            OnboardingNavigationFooter(
                pagerState = pagerState,
                canGoForward = canGoForward,
                isLastPage = pagerState.currentPage == 5,
                onNext = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    if (pagerState.currentPage < 5) {
                        scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                    } else {
                        scope.launch {
                            viewModel.saveUserProfile(
                                UserProfile(
                                    firstName = firstName,
                                    lastName = lastName,
                                    email = email,
                                    dateOfBirth = dateOfBirth,
                                    gender = selectedGender,
                                    occupation = occupation,
                                    isOnboardingComplete = true
                                )
                            )
                            viewModel.updateSettings(
                                viewModel.settings.value.copy(
                                    themeMode = selectedTheme,
                                    isOnboardingCompleted = true
                                )
                            )
                            viewModel.setOnboardingComplete(true)
                            delay(800)
                            viewModel.syncToCloud()
                            viewModel.syncFromCloud()
                        }
                    }
                },
                onBack = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    if (pagerState.currentPage > 0) {
                        scope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) }
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingTopBar(
    pagerState: PagerState,
    onSkip: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Progress Dots
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            repeat(pagerState.pageCount) { index ->
                val isActive = index == pagerState.currentPage
                val width by animateDpAsState(if (isActive) 16.dp else 6.dp)
                val alpha by animateFloatAsState(if (isActive) 1f else 0.3f)
                
                Box(
                    modifier = Modifier
                        .width(width)
                        .height(6.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = alpha))
                )
            }
        }
        
        // Skip Button - Only show if allowed
        val canSkip = pagerState.currentPage != 1 && pagerState.currentPage != 2 && pagerState.currentPage != 5
        if (canSkip) {
            TextButton(
                onClick = onSkip,
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.onSurfaceVariant)
            ) {
                Text("Skip", fontWeight = FontWeight.Bold)
            }
        } else {
            Spacer(modifier = Modifier.width(48.dp))
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingNavigationFooter(
    pagerState: PagerState,
    canGoForward: Boolean,
    isLastPage: Boolean,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 24.dp, end = 24.dp, bottom = 40.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Back Button
        if (pagerState.currentPage > 0) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.1f))
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        } else {
            Spacer(modifier = Modifier.size(56.dp))
        }

        // Center Action - Get Started or Next
        if (isLastPage) {
            Button(
                onClick = onNext,
                modifier = Modifier
                    .height(56.dp)
                    .padding(horizontal = 24.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Get Started", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.Default.ChevronRight, null)
            }
        } else {
            // "Right swipe" inspired button
            Surface(
                onClick = onNext,
                enabled = canGoForward,
                modifier = Modifier
                    .size(72.dp)
                    .shadow(if (canGoForward) 12.dp else 0.dp, CircleShape, spotColor = MaterialTheme.colorScheme.primary),
                shape = CircleShape,
                color = if (canGoForward) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "Next",
                        tint = if (canGoForward) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }

        // Symmetry spacer
        Spacer(modifier = Modifier.size(56.dp))
    }
}

@Composable
fun AnimatedBackground() {
    val infiniteTransition = rememberInfiniteTransition(label = "background")
    
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "angle"
    )

    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Box(modifier = Modifier.fillMaxSize().blur(60.dp)) {
        androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize().graphicsLayer {
            scaleX = scale
            scaleY = scale
            rotationZ = angle
        }) {
            val colors = listOf(
                Color(0xFF6366F1), // Indigo
                Color(0xFFA855F7), // Purple
                Color(0xFFEC4899), // Pink
                Color(0xFF3B82F6), // Blue
                Color(0xFF10B981)  // Emerald
            )
            
            // Draw multiple overlapping vibrant circles
            drawCircle(
                brush = Brush.radialGradient(listOf(colors[0].copy(alpha = 0.4f), Color.Transparent)),
                radius = size.width * 0.8f,
                center = androidx.compose.ui.geometry.Offset(size.width * 0.2f, size.height * 0.2f)
            )
            
            drawCircle(
                brush = Brush.radialGradient(listOf(colors[1].copy(alpha = 0.4f), Color.Transparent)),
                radius = size.width * 0.9f,
                center = androidx.compose.ui.geometry.Offset(size.width * 0.8f, size.height * 0.3f)
            )
            
            drawCircle(
                brush = Brush.radialGradient(listOf(colors[2].copy(alpha = 0.4f), Color.Transparent)),
                radius = size.width * 0.7f,
                center = androidx.compose.ui.geometry.Offset(size.width * 0.5f, size.height * 0.7f)
            )
            
            drawCircle(
                brush = Brush.radialGradient(listOf(colors[3].copy(alpha = 0.4f), Color.Transparent)),
                radius = size.width * 0.6f,
                center = androidx.compose.ui.geometry.Offset(size.width * 0.1f, size.height * 0.8f)
            )

            drawCircle(
                brush = Brush.radialGradient(listOf(colors[4].copy(alpha = 0.4f), Color.Transparent)),
                radius = size.width * 0.5f,
                center = androidx.compose.ui.geometry.Offset(size.width * 0.9f, size.height * 0.9f)
            )
        }
        
        // Add a subtle grain/noise effect if we had a drawable, but with canvas we can draw points
        // For performance, let's keep it clean but add a very light overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.15f))
        )
    }
}
