package com.lssgoo.planner.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * A reusable Icon Selector component that displays icons in categorized groups.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IconSelector(
    selectedIcon: String,
    onIconSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    selectedColor: Color = MaterialTheme.colorScheme.primary
) {
    var selectedCategory by remember { mutableStateOf(AppIcons.SelectionGroups.keys.first()) }

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            "Select Icon",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(12.dp))

        // Category Tabs
        ScrollableTabRow(
            selectedTabIndex = AppIcons.SelectionGroups.keys.indexOf(selectedCategory),
            containerColor = Color.Transparent,
            edgePadding = 0.dp,
            divider = {},
            indicator = { tabPositions ->
                 val index = AppIcons.SelectionGroups.keys.indexOf(selectedCategory)
                 TabRowDefaults.SecondaryIndicator(
                     modifier = Modifier.tabIndicatorOffset(tabPositions[index]),
                     color = selectedColor
                 )
            }
        ) {
            AppIcons.SelectionGroups.keys.forEach { category ->
                val isSelected = selectedCategory == category
                Tab(
                    selected = isSelected,
                    onClick = { selectedCategory = category },
                    text = {
                        Text(
                            text = category.split("&").first().trim(),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Icons Grid
        val icons = AppIcons.SelectionGroups[selectedCategory] ?: emptyList()
        
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 56.dp),
            modifier = Modifier.heightIn(max = 240.dp),
            contentPadding = PaddingValues(4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(icons) { iconName ->
                val isSelected = selectedIcon == iconName
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            if (isSelected) selectedColor.copy(alpha = 0.15f)
                            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        )
                        .border(
                            width = if (isSelected) 2.dp else 0.dp,
                            color = if (isSelected) selectedColor else Color.Transparent,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .clickable { onIconSelected(iconName) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = AppIcons.fromName(iconName),
                        contentDescription = iconName,
                        tint = if (isSelected) selectedColor else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    }
}
