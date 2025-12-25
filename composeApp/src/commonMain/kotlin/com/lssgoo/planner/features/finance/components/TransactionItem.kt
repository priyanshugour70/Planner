package com.lssgoo.planner.features.finance.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lssgoo.planner.data.model.Transaction
import com.lssgoo.planner.data.model.TransactionType
import com.lssgoo.planner.ui.components.AppIcons
import com.lssgoo.planner.ui.theme.FinanceColors
import com.lssgoo.planner.util.KmpDateFormatter

/**
 * Transaction item component
 * Displays transaction information in a card
 */
@Composable
fun TransactionItem(
    transaction: Transaction,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val color = when (transaction.type) {
        TransactionType.INCOME -> FinanceColors.income
        TransactionType.EXPENSE -> FinanceColors.expense
        TransactionType.BORROWED -> FinanceColors.borrowed
        TransactionType.LENT -> FinanceColors.lent
    }
    
    val prefix = when (transaction.type) {
        TransactionType.INCOME -> "+"
        TransactionType.EXPENSE -> "-"
        TransactionType.BORROWED -> "+"
        TransactionType.LENT -> "-"
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = AppIcons.fromName(transaction.category.iconName),
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(24.dp)
                )
                if (transaction.isRecurring) {
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = "Recurring",
                        modifier = Modifier
                            .size(12.dp)
                            .align(Alignment.TopEnd)
                            .padding(2.dp),
                        tint = color
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = transaction.note.ifBlank { 
                            transaction.category.name.lowercase().replaceFirstChar { it.uppercase() } 
                        },
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    if (transaction.receiptUri != null) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            Icons.Default.Image,
                            contentDescription = "Has receipt",
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = KmpDateFormatter.formatDateWithTime(transaction.date),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    // Show person name for debts
                    transaction.personName?.let { name ->
                        Text(
                            text = "• $name",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "$prefix ₹${String.format("%.2f", transaction.amount)}",
                    color = color,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
                
                // Show transaction type label for debts
                if (transaction.type == TransactionType.BORROWED || transaction.type == TransactionType.LENT) {
                    Text(
                        text = transaction.type.name.lowercase().replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
