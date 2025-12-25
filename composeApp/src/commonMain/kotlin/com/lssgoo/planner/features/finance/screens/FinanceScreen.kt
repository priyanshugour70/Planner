package com.lssgoo.planner.features.finance.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lssgoo.planner.data.model.*
import com.lssgoo.planner.features.finance.components.*
import com.lssgoo.planner.features.finance.models.DateRange
import com.lssgoo.planner.features.finance.models.DateRangeFilter
import com.lssgoo.planner.features.finance.models.SavingsGoal
import com.lssgoo.planner.features.finance.utils.FinanceDateUtils
import com.lssgoo.planner.ui.components.GradientFAB
import com.lssgoo.planner.ui.theme.GradientColors
import com.lssgoo.planner.ui.viewmodel.PlannerViewModel

/**
 * Main Finance Screen - Integrated with all new components
 * Supports: Date filtering, Edit/Delete with confirmations, Savings management
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinanceScreen(
    viewModel: PlannerViewModel,
    modifier: Modifier = Modifier
) {
    val stats by viewModel.financeStats.collectAsState()
    val transactions by viewModel.transactions.collectAsState()
    val budgets by viewModel.budgets.collectAsState()
    
    // Mock savings goals for now (will be added to ViewModel later)
    val savingsGoals = remember { mutableStateListOf<SavingsGoal>() }
    
    var selectedTab by remember { mutableIntStateOf(0) }
    var dateRange by remember { mutableStateOf(FinanceDateUtils.getDateRange(DateRangeFilter.THIS_MONTH)) }
    
    // Dialog states
    var showTransactionDialog by remember { mutableStateOf(false) }
    var editingTransaction by remember { mutableStateOf<Transaction?>(null) }
    var showBudgetDialog by remember { mutableStateOf(false) }
    var editingBudget by remember { mutableStateOf<Budget?>(null) }
    var showSavingsDialog by remember { mutableStateOf(false) }
    var editingSavingsGoal by remember { mutableStateOf<SavingsGoal?>(null) }
    var showSavingsTransactionDialog by remember { mutableStateOf(false) }
    var selectedSavingsGoal by remember { mutableStateOf<SavingsGoal?>(null) }
    
    val tabs = listOf("Dashboard", "Analysis", "Transactions", "Budgets", "Debts", "Savings")

    Scaffold(
        topBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.background
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(horizontal = 20.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.AccountBalanceWallet,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Finance",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        IconButton(onClick = { 
                            // TODO: PDF Export
                            // showPDFExportDialog = true
                        }) {
                            Icon(
                                Icons.Default.Share,
                                contentDescription = "Export",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    
                    // Date Range Selector
                    DateRangeSelector(
                        currentRange = dateRange,
                        onRangeSelected = { dateRange = it },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }
        },
        floatingActionButton = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Context-aware FABs
                when (selectedTab) {
                    3 -> { // Budgets tab
                        GradientFAB(
                            onClick = { showBudgetDialog = true },
                            icon = Icons.Filled.PlaylistAdd,
                            gradientColors = GradientColors.purpleBlue
                        )
                    }
                    5 -> { // Savings tab
                        GradientFAB(
                            onClick = { showSavingsDialog = true },
                            icon = Icons.Filled.Savings,
                            gradientColors = GradientColors.orangePink
                        )
                    }
                }
                // Always show transaction FAB
                GradientFAB(
                    onClick = { showTransactionDialog = true },
                    icon = Icons.Filled.Add,
                    gradientColors = GradientColors.oceanBlue
                )
            }
        },
        modifier = modifier
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                divider = {},
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                edgePadding = 16.dp
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { 
                            Text(
                                title, 
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                            ) 
                        }
                    )
                }
            }
            
            Box(modifier = Modifier.fillMaxSize()) {
                when (selectedTab) {
                    0 -> DashboardTab(stats, savingsGoals)
                    1 -> AnalysisTab(stats)
                    2 -> TransactionsTab(
                        transactions = transactions,
                        dateRange = dateRange,
                        onEditTransaction = { editingTransaction = it },
                        onDeleteTransaction = { viewModel.deleteTransaction(it) }
                    )
                    3 -> BudgetsTab(
                        budgets = budgets,
                        onEditBudget = { editingBudget = it },
                        onDeleteBudget = { viewModel.deleteBudget(it) }
                    )
                    4 -> DebtsTabContent(
                        transactions = transactions.filter { 
                            (it.type == TransactionType.BORROWED || it.type == TransactionType.LENT) && !it.isSettled 
                        },
                        onSettle = { viewModel.settleDebt(it) }
                    )
                    5 -> SavingsTab(
                        savingsGoals = savingsGoals,
                        onAddGoal = { showSavingsDialog = true },
                        onEditGoal = { editingSavingsGoal = it },
                        onDeleteGoal = { id ->
                            savingsGoals.removeAll { it.id == id }
                        },
                        onAddMoney = { goal ->
                            selectedSavingsGoal = goal
                            showSavingsTransactionDialog = true
                        }
                    )
                }
            }
        }
    }
    
    // Transaction Dialog (Add or Edit)
    if (showTransactionDialog || editingTransaction != null) {
        TransactionDialog(
            transaction = editingTransaction,
            onDismiss = { 
                showTransactionDialog = false
                editingTransaction = null
            },
            onSave = { transaction ->
                if (editingTransaction != null) {
                    viewModel.updateTransaction(transaction)
                } else {
                    viewModel.addTransaction(transaction)
                }
                showTransactionDialog = false
                editingTransaction = null
            },
            onDelete = if (editingTransaction != null) {
                { id -> viewModel.deleteTransaction(id) }
            } else null
        )
    }
    
    // Budget Dialog (Add or Edit)
    if (showBudgetDialog || editingBudget != null) {
        BudgetDialog(
            budget = editingBudget,
            onDismiss = { 
                showBudgetDialog = false
                editingBudget = null
            },
            onSave = { budget ->
                if (editingBudget != null) {
                    viewModel.updateBudget(budget)
                } else {
                    viewModel.addBudget(budget)
                }
                showBudgetDialog = false
                editingBudget = null
            },
            onDelete = if (editingBudget != null) {
                { id -> viewModel.deleteBudget(id) }
            } else null
        )
    }
    
    // Savings Goal Dialog (Add or Edit)
    if (showSavingsDialog || editingSavingsGoal != null) {
        SavingsGoalDialog(
            savingsGoal = editingSavingsGoal,
            onDismiss = { 
                showSavingsDialog = false
                editingSavingsGoal = null
            },
            onSave = { goal ->
                if (editingSavingsGoal != null) {
                    val index = savingsGoals.indexOfFirst { it.id == goal.id }
                    if (index != -1) savingsGoals[index] = goal
                } else {
                    savingsGoals.add(goal)
                }
                showSavingsDialog = false
                editingSavingsGoal = null
            },
            onDelete = if (editingSavingsGoal != null) {
                { id ->
                    savingsGoals.removeAll { it.id == id }
                }
            } else null
        )
    }
    
    // Savings Transaction Dialog (Deposit/Withdraw)
    if (showSavingsTransactionDialog && selectedSavingsGoal != null) {
        SavingsTransactionDialog(
            savingsGoal = selectedSavingsGoal!!,
            onDismiss = { 
                showSavingsTransactionDialog = false
                selectedSavingsGoal = null
            },
            onSave = { amount, isDeposit, note ->
                val index = savingsGoals.indexOfFirst { it.id == selectedSavingsGoal!!.id }
                if (index != -1) {
                    val currentGoal = savingsGoals[index]
                    val newAmount = if (isDeposit) {
                        currentGoal.currentAmount + amount
                    } else {
                        (currentGoal.currentAmount - amount).coerceAtLeast(0.0)
                    }
                    savingsGoals[index] = currentGoal.copy(currentAmount = newAmount)
                }
                showSavingsTransactionDialog = false
                selectedSavingsGoal = null
            }
        )
    }
}

@Composable
private fun DebtsTabContent(
    transactions: List<Transaction>,
    onSettle: (String) -> Unit
) {
    if (transactions.isEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                Icons.Default.Handshake,
                contentDescription = null,
                modifier = Modifier.size(120.dp),
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                "No Outstanding Debts",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(transactions) { debt ->
                var showSettleConfirm by remember { mutableStateOf(false) }
                
                TransactionItem(
                    transaction = debt,
                    onClick = { showSettleConfirm = true }
                )
                
                if (showSettleConfirm) {
                    DeleteConfirmationDialog(
                        title = "Settle Debt?",
                        message = "Mark this debt as settled?",
                        onConfirm = {
                            onSettle(debt.id)
                            showSettleConfirm = false
                        },
                        onDismiss = { showSettleConfirm = false }
                    )
                }
            }
        }
    }
}
