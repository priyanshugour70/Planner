package com.lssgoo.planner.features.finance.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.lssgoo.planner.data.model.*
import com.lssgoo.planner.ui.components.AppIcons
import com.lssgoo.planner.util.KmpTimeUtils

/**
 * Transaction dialog for Add and Edit operations
 * Following SRP - handles only transaction form UI
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionDialog(
    transaction: Transaction? = null, // null = Add mode, non-null = Edit mode
    onDismiss: () -> Unit,
    onSave: (Transaction) -> Unit,
    onDelete: ((String) -> Unit)? = null
) {
    val isEditMode = transaction != null
    
    var amount by remember { mutableStateOf(transaction?.amount?.toString() ?: "") }
    var note by remember { mutableStateOf(transaction?.note ?: "") }
    var selectedType by remember { mutableStateOf(transaction?.type ?: TransactionType.EXPENSE) }
    var selectedCategory by remember { mutableStateOf(transaction?.category ?: TransactionCategory.FOOD) }
    var personName by remember { mutableStateOf(transaction?.personName ?: "") }
    var isRecurring by remember { mutableStateOf(transaction?.isRecurring ?: false) }
    var recurringPeriod by remember { mutableStateOf(transaction?.recurringPeriod ?: BudgetPeriod.MONTHLY) }
    var receiptUri by remember { mutableStateOf(transaction?.receiptUri) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var showSavingsOption by remember { mutableStateOf(false) }
    var isSavingsDeposit by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .heightIn(max = 700.dp),
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
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.secondary
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
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(Color.White.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Receipt,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    if (isEditMode) "Edit Transaction" else "Add Transaction",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    if (isEditMode) "Update your record" else "Track your money flow",
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
                
                // Content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Transaction Type - Segmented Button Style
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            "Transaction Type",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            TransactionType.entries.forEach { type ->
                                val isSelected = selectedType == type
                                Surface(
                                    onClick = { selectedType = type },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
                                ) {
                                    Column(
                                        modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(
                                            imageVector = when(type) {
                                                TransactionType.INCOME -> Icons.Default.ArrowDownward
                                                TransactionType.EXPENSE -> Icons.Default.ArrowUpward
                                                TransactionType.BORROWED -> Icons.Default.CallReceived
                                                TransactionType.LENT -> Icons.Default.CallMade
                                            },
                                            contentDescription = null,
                                            tint = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = when(type) {
                                                TransactionType.INCOME -> "Income"
                                                TransactionType.EXPENSE -> "Expense"
                                                TransactionType.BORROWED -> "Borrowed"
                                                TransactionType.LENT -> "Lent"
                                            },
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 1
                                        )
                                    }
                                }
                            }
                        }
                    }
                    
                    // Amount Input
                    OutlinedTextField(
                        value = amount,
                        onValueChange = { if (it.isEmpty() || it.toDoubleOrNull() != null) amount = it },
                        label = { Text("Amount") },
                        placeholder = { Text("0.00") },
                        leadingIcon = { 
                            Text(
                                "₹", 
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        isError = amount.isNotEmpty() && amount.toDoubleOrNull() == null
                    )
                    
                    // Note Input
                    OutlinedTextField(
                        value = note,
                        onValueChange = { note = it },
                        label = { Text("Note / Description") },
                        placeholder = { Text("What was this for?") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                        maxLines = 3,
                        shape = RoundedCornerShape(14.dp)
                    )
                    
                    // Savings Option (for Income)
                    if (selectedType == TransactionType.INCOME) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.Savings,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.tertiary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        "Add to Savings",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                                Switch(
                                    checked = isSavingsDeposit,
                                    onCheckedChange = { isSavingsDeposit = it }
                                )
                            }
                        }
                    }
                    
                    // Recurring Transaction Switch
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Repeat,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    "Recurring Transaction",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            Switch(
                                checked = isRecurring,
                                onCheckedChange = { isRecurring = it }
                            )
                        }
                    }
                    
                    if (isRecurring) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            BudgetPeriod.entries.forEach { period ->
                                val isSelected = recurringPeriod == period
                                Surface(
                                    onClick = { recurringPeriod = period },
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                                ) {
                                    Text(
                                        text = period.name.lowercase().replaceFirstChar { it.uppercase() },
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }

                    // Receipt Button
                    OutlinedButton(
                        onClick = { receiptUri = if (receiptUri == null) "mock_uri_${KmpTimeUtils.currentTimeMillis()}" else null },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(
                            1.dp, 
                            if (receiptUri != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                        )
                    ) {
                        Icon(
                            if (receiptUri != null) Icons.Default.CheckCircle else Icons.Default.PhotoCamera,
                            contentDescription = null,
                            tint = if (receiptUri != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            if (receiptUri == null) "Attach Receipt" else "Receipt Attached ✓",
                            color = if (receiptUri != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Category Section
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            "Category",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                        
                        // Categories in scrollable rows
                        val categoriesPart1 = TransactionCategory.entries.take(TransactionCategory.entries.size / 2)
                        val categoriesPart2 = TransactionCategory.entries.drop(TransactionCategory.entries.size / 2)
                        
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            categoriesPart1.forEach { category ->
                                CategoryChip(
                                    category = category,
                                    isSelected = selectedCategory == category,
                                    onClick = { selectedCategory = category }
                                )
                            }
                        }
                        
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            categoriesPart2.forEach { category ->
                                CategoryChip(
                                    category = category,
                                    isSelected = selectedCategory == category,
                                    onClick = { selectedCategory = category }
                                )
                            }
                        }
                    }
                    
                    // Person Name for debt
                    if (selectedType == TransactionType.LENT || selectedType == TransactionType.BORROWED) {
                        OutlinedTextField(
                            value = personName,
                            onValueChange = { personName = it },
                            label = { Text("Person Name") },
                            placeholder = { Text("Who is involved?") },
                            leadingIcon = {
                                Icon(Icons.Default.Person, contentDescription = null)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp)
                        )
                    }
                }
                
                // Action Buttons
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = {
                            val amt = amount.toDoubleOrNull() ?: 0.0
                            val newTransaction = if (isEditMode && transaction != null) {
                                transaction.copy(
                                    amount = amt,
                                    type = selectedType,
                                    category = selectedCategory,
                                    note = note,
                                    personName = personName.ifBlank { null },
                                    isRecurring = isRecurring,
                                    recurringPeriod = if (isRecurring) recurringPeriod else null,
                                    receiptUri = receiptUri,
                                    updatedAt = KmpTimeUtils.currentTimeMillis()
                                )
                            } else {
                                Transaction(
                                    amount = amt,
                                    type = selectedType,
                                    category = selectedCategory,
                                    note = note,
                                    personName = personName.ifBlank { null },
                                    isRecurring = isRecurring,
                                    recurringPeriod = if (isRecurring) recurringPeriod else null,
                                    receiptUri = receiptUri
                                )
                            }
                            onSave(newTransaction)
                        },
                        enabled = amount.isNotEmpty() && amount.toDoubleOrNull() != null,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(Icons.Default.Check, null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (isEditMode) "Update" else "Save")
                    }
                }
            }
        }
    }
    
    // Delete Confirmation Dialog
    if (showDeleteConfirm && isEditMode && transaction != null && onDelete != null) {
        DeleteConfirmationDialog(
            title = "Delete Transaction?",
            message = "Are you sure you want to delete this transaction? This action cannot be undone.",
            onConfirm = {
                onDelete(transaction.id)
                showDeleteConfirm = false
                onDismiss()
            },
            onDismiss = { showDeleteConfirm = false }
        )
    }
}

@Composable
private fun CategoryChip(
    category: TransactionCategory,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
        border = if (isSelected) BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary) else null
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = AppIcons.fromName(category.iconName),
                contentDescription = null,
                tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                category.name.lowercase().replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.bodySmall,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                maxLines = 1
            )
        }
    }
}
