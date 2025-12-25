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
import com.lssgoo.planner.data.model.Budget
import com.lssgoo.planner.features.finance.components.BudgetProgressItem

/**
 * Budgets Tab - Budget list management
 * Following SRP - handles only budget list UI
 */
@Composable
fun BudgetsTab(
    budgets: List<Budget>,
    onEditBudget: (Budget) -> Unit,
    onDeleteBudget: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (budgets.isEmpty()) {
        EmptyBudgetsState()
    } else {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(budgets.filter { !it.isDeleted }) { budget ->
                BudgetProgressItem(
                    budget = budget,
                    showDelete = true,
                    onDelete = { onDeleteBudget(budget.id) },
                    onClick = { onEditBudget(budget) }
                )
            }
        }
    }
}

@Composable
private fun EmptyBudgetsState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.AccountBalance,
            contentDescription = null,
            modifier = Modifier.size(120.dp),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            "No Budgets Set",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Create budgets to track and control your spending",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}
