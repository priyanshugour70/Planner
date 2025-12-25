package com.lssgoo.planner.features.finance.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.lssgoo.planner.data.model.*
import com.lssgoo.planner.ui.components.AppIcons
import com.lssgoo.planner.util.KmpTimeUtils

/**
 * Budget dialog for Add and Edit operations
 * Following SRP - handles only budget form UI
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetDialog(
    budget: Budget? = null, // null = Add mode, non-null = Edit mode
    onDismiss: () -> Unit,
    onSave: (Budget) -> Unit,
    onDelete: ((String) -> Unit)? = null
) {
    val isEditMode = budget != null
    
    var limit by remember { mutableStateOf(budget?.limitAmount?.toString() ?: "") }
    var selectedCategory by remember { mutableStateOf<TransactionCategory?>(budget?.category) }
    var selectedPeriod by remember { mutableStateOf(budget?.period ?: BudgetPeriod.MONTHLY) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .wrapContentHeight(),
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column {
                // Header with gradient
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = androidx.compose.ui.graphics.Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFF4CAF50),
                                    Color(0xFF00BCD4)
                                )
                            )
                        )
                        .padding(24.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = Color.White.copy(alpha = 0.2f),
                                modifier = Modifier.size(48.dp)
                            ) {
                                Icon(
                                    AppIcons.AccountBalanceWallet,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    if (isEditMode) "Edit Budget" else "Set Budget",
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    if (isEditMode) "Update your spending limit" else "Control your spending",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                            }
                        }
                        
                        if (isEditMode && onDelete != null) {
                            IconButton(onClick = { showDeleteConfirm = true }) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = Color.White.copy(alpha = 0.9f)
                                )
                            }
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Amount Input
                    OutlinedTextField(
                        value = limit,
                        onValueChange = { if (it.isEmpty() || it.toDoubleOrNull() != null) limit = it },
                        label = { Text("Budget Limit") },
                        modifier = Modifier.fillMaxWidth(),
                        prefix = { Text("₹ ") },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        ),
                        shape = RoundedCornerShape(16.dp),
                        leadingIcon = {
                            Icon(
                                AppIcons.Payments,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        isError = limit.isNotEmpty() && limit.toDoubleOrNull() == null
                    )
                    
                    // Period Selector
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            "Budget Period",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            BudgetPeriod.entries.forEach { period ->
                                val isSelected = selectedPeriod == period
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { selectedPeriod = period },
                                    label = { Text(period.name.lowercase().replaceFirstChar { it.uppercase() }) },
                                    shape = RoundedCornerShape(12.dp),
                                    leadingIcon = if (isSelected) {
                                        { Icon(Icons.Default.Check, null, modifier = Modifier.size(18.dp)) }
                                    } else null
                                )
                            }
                        }
                    }

                    // Category Selector
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            "Select Category (optional)",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Overall Option
                            FilterChip(
                                selected = selectedCategory == null,
                                onClick = { selectedCategory = null },
                                label = { Text("Overall Balance") },
                                shape = RoundedCornerShape(12.dp),
                                leadingIcon = {
                                    Icon(
                                        AppIcons.DonutLarge,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            )

                            TransactionCategory.entries.forEach { category ->
                                val isSelected = selectedCategory == category
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { selectedCategory = category },
                                    label = { Text(category.name.lowercase().replaceFirstChar { it.uppercase() }) },
                                    shape = RoundedCornerShape(12.dp),
                                    leadingIcon = {
                                        Icon(
                                            imageVector = AppIcons.fromName(category.iconName),
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text("Cancel")
                        }
                        
                        Button(
                            onClick = {
                                val lim = limit.toDoubleOrNull() ?: 0.0
                                val newBudget = if (isEditMode && budget != null) {
                                    budget.copy(
                                        category = selectedCategory,
                                        limitAmount = lim,
                                        period = selectedPeriod,
                                        updatedAt = KmpTimeUtils.currentTimeMillis()
                                    )
                                } else {
                                    Budget(
                                        category = selectedCategory,
                                        limitAmount = lim,
                                        period = selectedPeriod
                                    )
                                }
                                onSave(newBudget)
                            },
                            enabled = limit.isNotEmpty() && limit.toDoubleOrNull() != null,
                            modifier = Modifier
                                .weight(1.5f)
                                .height(56.dp),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(if (isEditMode) "Update" else "Create Budget")
                        }
                    }
                }
            }
        }
    }
    
    // Delete Confirmation Dialog
    if (showDeleteConfirm && isEditMode && budget != null && onDelete != null) {
        DeleteConfirmationDialog(
            title = "Delete Budget?",
            message = "Are you sure you want to delete this budget? This action cannot be undone.",
            onConfirm = {
                onDelete(budget.id)
                showDeleteConfirm = false
                onDismiss()
            },
            onDismiss = { showDeleteConfirm = false }
        )
    }
}
