package com.lssgoo.planner.ui.viewmodel

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.lssgoo.planner.data.local.LocalStorageManager
import com.lssgoo.planner.data.model.*
import com.lssgoo.planner.data.repository.FinanceRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for Finance feature
 */
class FinanceViewModel(application: Application) : BaseViewModel(application) {
    
    private val storageManager = LocalStorageManager(application)
    private val financeRepository = FinanceRepository(storageManager)
    
    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions.asStateFlow()
    
    private val _budgets = MutableStateFlow<List<Budget>>(emptyList())
    val budgets: StateFlow<List<Budget>> = _budgets.asStateFlow()
    
    private val _financeStats = MutableStateFlow(FinanceStats())
    val financeStats: StateFlow<FinanceStats> = _financeStats.asStateFlow()
    
    init {
        loadFinanceData()
    }
    
    fun loadFinanceData() {
        viewModelScope.launch(Dispatchers.IO) {
            _transactions.value = financeRepository.getTransactions()
            _budgets.value = financeRepository.getBudgets()
            _financeStats.value = financeRepository.getFinanceStats()
        }
    }
    
    fun addTransaction(transaction: Transaction) {
        viewModelScope.launch(Dispatchers.IO) {
            financeRepository.addTransaction(transaction)
            loadFinanceData()
        }
    }
    
    fun deleteTransaction(transactionId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            financeRepository.deleteTransaction(transactionId)
            loadFinanceData()
        }
    }
    
    fun addBudget(budget: Budget) {
        viewModelScope.launch(Dispatchers.IO) {
            financeRepository.addBudget(budget)
            loadFinanceData()
        }
    }
    
    fun removeBudget(budgetId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            financeRepository.removeBudget(budgetId)
            loadFinanceData()
        }
    }
}
