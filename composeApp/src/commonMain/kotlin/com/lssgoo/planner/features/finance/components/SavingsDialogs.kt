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
import com.lssgoo.planner.features.finance.models.SavingsGoal
import com.lssgoo.planner.ui.components.AppIcons
import com.lssgoo.planner.util.KmpDateFormatter
import com.lssgoo.planner.util.KmpTimeUtils

/**
 * Savings Goal dialog for Add and Edit operations
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavingsGoalDialog(
    savingsGoal: SavingsGoal? = null, // null = Add mode
    onDismiss: () -> Unit,
    onSave: (SavingsGoal) -> Unit,
    onDelete: ((String) -> Unit)? = null
) {
    val isEditMode = savingsGoal != null
    
    var name by remember { mutableStateOf(savingsGoal?.name ?: "") }
    var targetAmount by remember { mutableStateOf(savingsGoal?.targetAmount?.toString() ?: "") }
    var category by remember { mutableStateOf(savingsGoal?.category ?: "General") }
    var deadline by remember { mutableStateOf(savingsGoal?.deadline) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    
    val categories = listOf("General", "Emergency Fund", "Vacation", "Education", "Home", "Car", "Retirement", "Other")

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
                // Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = androidx.compose.ui.graphics.Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFFFF6B6B),
                                    Color(0xFFFFD93D)
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
                                    Icons.Default.Savings,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    if (isEditMode) "Edit Savings Goal" else "New Savings Goal",
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    "Build your financial future",
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
                    // Goal Name
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Goal Name") },
                        placeholder = { Text("e.g., Emergency Fund") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        leadingIcon = {
                            Icon(Icons.Default.Flag, null, tint = MaterialTheme.colorScheme.primary)
                        }
                    )
                    
                    // Target Amount
                    OutlinedTextField(
                        value = targetAmount,
                        onValueChange = { if (it.isEmpty() || it.toDoubleOrNull() != null) targetAmount = it },
                        label = { Text("Target Amount") },
                        placeholder = { Text("0.00") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        prefix = { Text("₹ ") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(16.dp),
                        leadingIcon = {
                            Icon(Icons.Default.TrendingUp, null, tint = MaterialTheme.colorScheme.primary)
                        },
                        isError = targetAmount.isNotEmpty() && targetAmount.toDoubleOrNull() == null
                    )
                    
                    // Category Selection - Themed
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            "Select Category",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            categories.forEach { cat ->
                                val isSelected = category == cat
                                Surface(
                                    onClick = { category = cat },
                                    shape = RoundedCornerShape(14.dp),
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    modifier = Modifier.widthIn(min = 100.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Text(
                                            cat,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                    
                    // Deadline (Optional)
                    OutlinedButton(
                        onClick = { showDatePicker = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(Icons.Default.CalendarToday, null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            deadline?.let { "Deadline: ${KmpDateFormatter.formatDate(it)}" } 
                                ?: "Set Deadline (Optional)"
                        )
                        if (deadline != null) {
                            Spacer(modifier = Modifier.weight(1f))
                            IconButton(
                                onClick = { deadline = null },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(Icons.Default.Close, "Clear", modifier = Modifier.size(16.dp))
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
                                val target = targetAmount.toDoubleOrNull() ?: 0.0
                                val newGoal = if (isEditMode && savingsGoal != null) {
                                    savingsGoal.copy(
                                        name = name,
                                        targetAmount = target,
                                        category = category,
                                        deadline = deadline,
                                        updatedAt = KmpTimeUtils.currentTimeMillis()
                                    )
                                } else {
                                    SavingsGoal(
                                        name = name,
                                        targetAmount = target,
                                        category = category,
                                        deadline = deadline
                                    )
                                }
                                onSave(newGoal)
                            },
                            enabled = name.isNotBlank() && targetAmount.isNotEmpty() && targetAmount.toDoubleOrNull() != null,
                            modifier = Modifier
                                .weight(1.5f)
                                .height(56.dp),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Icon(Icons.Default.Check, null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(if (isEditMode) "Update" else "Create Goal")
                        }
                    }
                }
            }
        }
    }
    
    // Date Picker
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = deadline ?: KmpTimeUtils.currentTimeMillis()
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    deadline = datePickerState.selectedDateMillis
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
    
    // Delete Confirmation
    if (showDeleteConfirm && isEditMode && savingsGoal != null && onDelete != null) {
        DeleteConfirmationDialog(
            title = "Delete Savings Goal?",
            message = "Are you sure you want to delete \"${savingsGoal.name}\"? This will also delete all associated transactions.",
            onConfirm = {
                onDelete(savingsGoal.id)
                showDeleteConfirm = false
                onDismiss()
            },
            onDismiss = { showDeleteConfirm = false }
        )
    }
}

/**
 * Dialog for adding money to or withdrawing from savings
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavingsTransactionDialog(
    savingsGoal: SavingsGoal,
    onDismiss: () -> Unit,
    onSave: (Double, Boolean, String) -> Unit // amount, isDeposit, note
) {
    var amount by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var isDeposit by remember { mutableStateOf(true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { 
            Text(
                if (isDeposit) "Add to ${savingsGoal.name}" else "Withdraw from ${savingsGoal.name}",
                fontWeight = FontWeight.Bold
            ) 
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Deposit/Withdraw Toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
                ) {
                    Surface(
                        onClick = { isDeposit = true },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        color = if (isDeposit) MaterialTheme.colorScheme.primary else Color.Transparent
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Add,
                                null,
                                tint = if (isDeposit) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                "Deposit",
                                color = if (isDeposit) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = if (isDeposit) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                    Surface(
                        onClick = { isDeposit = false },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        color = if (!isDeposit) MaterialTheme.colorScheme.error else Color.Transparent
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Remove,
                                null,
                                tint = if (!isDeposit) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                "Withdraw",
                                color = if (!isDeposit) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = if (!isDeposit) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
                
                OutlinedTextField(
                    value = amount,
                    onValueChange = { if (it.isEmpty() || it.toDoubleOrNull() != null) amount = it },
                    label = { Text("Amount") },
                    prefix = { Text("₹ ") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = amount.isNotEmpty() && amount.toDoubleOrNull() == null
                )
                
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Note (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amt = amount.toDoubleOrNull() ?: 0.0
                    onSave(amt, isDeposit, note)
                },
                enabled = amount.isNotEmpty() && amount.toDoubleOrNull() != null
            ) {
                Text(if (isDeposit) "Add" else "Withdraw")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
