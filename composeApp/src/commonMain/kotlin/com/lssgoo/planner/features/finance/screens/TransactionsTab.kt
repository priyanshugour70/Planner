package com.lssgoo.planner.features.finance.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lssgoo.planner.data.model.Transaction
import com.lssgoo.planner.features.finance.components.TransactionItem
import com.lssgoo.planner.features.finance.models.DateRange
import com.lssgoo.planner.features.finance.utils.FinanceDateUtils
import com.lssgoo.planner.util.KmpDateFormatter
import com.lssgoo.planner.util.KmpTimeUtils
import kotlinx.datetime.*

/**
 * Transactions Tab - Transaction list with filtering
 * Following SRP - handles only transaction list UI
 */
@Composable
fun TransactionsTab(
    transactions: List<Transaction>,
    dateRange: DateRange,
    onEditTransaction: (Transaction) -> Unit,
    onDeleteTransaction: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Filter transactions by date range
    val filteredTransactions = remember(transactions, dateRange) {
        transactions.filter { transaction ->
            FinanceDateUtils.isInRange(transaction.date, dateRange)
        }
    }
    
    if (filteredTransactions.isEmpty()) {
        EmptyTransactionsState()
    } else {
        // Group by date
        val grouped = remember(filteredTransactions) {
            filteredTransactions.sortedByDescending { it.date }.groupBy { 
                val now = KmpTimeUtils.currentTimeMillis()
                val oneDayMs = 24 * 60 * 60 * 1000L
                val yesterday = now - oneDayMs
                
                when {
                    isSameDay(it.date, now) -> "Today"
                    isSameDay(it.date, yesterday) -> "Yesterday"
                    else -> KmpDateFormatter.formatMonthYear(
                        Instant.fromEpochMilliseconds(it.date).toLocalDateTime(TimeZone.currentSystemDefault()).year,
                        Instant.fromEpochMilliseconds(it.date).toLocalDateTime(TimeZone.currentSystemDefault()).monthNumber
                    )
                }
            }
        }

        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            grouped.forEach { (header, items) ->
                item {
                    Text(
                        header,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
                items(items) { transaction ->
                    TransactionItem(
                        transaction = transaction,
                        onClick = { onEditTransaction(transaction) }
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyTransactionsState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.AccountBalanceWallet,
            contentDescription = null,
            modifier = Modifier.size(120.dp),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            "No Transactions",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "No transactions found for the selected period",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

private fun isSameDay(d1: Long, d2: Long): Boolean {
    return KmpDateFormatter.isSameDay(d1, d2)
}
