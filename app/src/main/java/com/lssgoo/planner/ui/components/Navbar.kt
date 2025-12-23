package com.lssgoo.planner.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.lssgoo.planner.ui.navigation.BottomNavDestination
import com.lssgoo.planner.ui.navigation.Routes
import com.lssgoo.planner.ui.theme.RouteColors

/**
 * Dynamic Bottom Navigation Bar with iOS-like polish
 * Optimized for reuse and dynamic theming
 */
@Composable
fun DynamicBottomNavBar(
    navController: NavHostController,
    currentRoute: String?,
    modifier: Modifier = Modifier
) {
    val accentColor = getAccentColorForRoute(currentRoute)
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 20.dp,
                    shape = RoundedCornerShape(28.dp),
                    ambientColor = accentColor.copy(alpha = 0.3f),
                    spotColor = accentColor.copy(alpha = 0.3f)
                ),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
            )
        ) {
            val scrollState = rememberScrollState()
            val canScrollLeft by remember { derivedStateOf { scrollState.value > 0 } }
            val canScrollRight by remember { derivedStateOf { scrollState.canScrollForward } }
            val surfaceColor = MaterialTheme.colorScheme.surface
            
            Box(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .horizontalScroll(scrollState)
                        .padding(horizontal = 8.dp, vertical = 8.dp)
                        .drawWithContent {
                            drawContent()
                            if (canScrollLeft) {
                                drawRect(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(surfaceColor.copy(alpha = 0.95f), surfaceColor.copy(alpha = 0f)),
                                        startX = 0f,
                                        endX = 40.dp.toPx()
                                    )
                                )
                            }
                            if (canScrollRight) {
                                drawRect(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(surfaceColor.copy(alpha = 0f), surfaceColor.copy(alpha = 0.95f)),
                                        startX = size.width - 40.dp.toPx(),
                                        endX = size.width
                                    )
                                )
                            }
                        },
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BottomNavDestination.entries.forEach { destination ->
                        DynamicNavItem(
                            destination = destination,
                            isSelected = currentRoute == destination.route,
                            accentColor = accentColor,
                            onClick = {
                                if (currentRoute != destination.route) {
                                    navController.navigate(destination.route) {
                                        popUpTo(Routes.DASHBOARD) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DynamicNavItem(
    destination: BottomNavDestination,
    isSelected: Boolean,
    accentColor: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(
                if (isSelected) accentColor.copy(alpha = 0.15f) else Color.Transparent
            )
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            IconButton(onClick = onClick, modifier = Modifier.size(if (isSelected) 36.dp else 32.dp)) {
                Icon(
                    imageVector = if (isSelected) destination.selectedIcon else destination.unselectedIcon,
                    contentDescription = destination.label,
                    tint = if (isSelected) accentColor else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(if (isSelected) 26.dp else 24.dp)
                )
            }
            
            AnimatedVisibility(visible = isSelected) {
                Text(
                    text = destination.label,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = accentColor,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }
    }
}

@Composable
fun getAccentColorForRoute(route: String?): Color {
    return when (route) {
        Routes.DASHBOARD -> MaterialTheme.colorScheme.primary
        Routes.GOALS -> RouteColors.goals
        Routes.CALENDAR -> RouteColors.calendar
        Routes.NOTES -> RouteColors.notes
        Routes.TASKS -> RouteColors.tasks
        Routes.HABITS -> RouteColors.habits
        Routes.JOURNAL -> RouteColors.journal
        Routes.FINANCE -> RouteColors.finance
        Routes.SEARCH -> RouteColors.search
        Routes.ANALYTICS -> RouteColors.analytics
        Routes.SETTINGS -> RouteColors.settings
        else -> MaterialTheme.colorScheme.primary
    }
}
