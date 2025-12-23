package com.lssgoo.planner.data.repository

import com.lssgoo.planner.data.local.LocalStorageManager
import com.lssgoo.planner.data.model.*

/**
 * Repository for managing Finance data - part of the backend layer
 */
class FinanceRepository(private val storage: LocalStorageManager) {

    fun getTransactions(): List<Transaction> {
        return storage.getTransactions()
    }

    fun addTransaction(transaction: Transaction) {
        val list = storage.getTransactions().toMutableList()
        list.add(0, transaction)
        storage.saveTransactions(list)
        
        // Update budget spending if it's an expense
        if (transaction.type == TransactionType.EXPENSE) {
            updateBudgetSpending(transaction.category, transaction.amount)
        }
    }

    fun deleteTransaction(id: String) {
        val list = storage.getTransactions().toMutableList()
        val transaction = list.find { it.id == id }
        if (transaction != null) {
            list.remove(transaction)
            storage.saveTransactions(list)
            
            // Revert budget spending if it was an expense
            if (transaction.type == TransactionType.EXPENSE) {
                updateBudgetSpending(transaction.category, -transaction.amount)
            }
        }
    }

    fun getBudgets(): List<Budget> {
        return storage.getBudgets()
    }

    fun addBudget(budget: Budget) {
        val list = storage.getBudgets().toMutableList()
        list.add(budget)
        storage.saveBudgets(list)
    }

    fun removeBudget(id: String) {
        val list = storage.getBudgets().toMutableList()
        list.removeAll { it.id == id }
        storage.saveBudgets(list)
    }

    private fun updateBudgetSpending(category: TransactionCategory, amount: Double) {
        val budgets = storage.getBudgets().toMutableList()
        var changed = false
        for (i in budgets.indices) {
            if (budgets[i].category == category || budgets[i].category == null) {
                budgets[i] = budgets[i].copy(spentAmount = budgets[i].spentAmount + amount)
                changed = true
            }
        }
        if (changed) storage.saveBudgets(budgets)
    }

    fun getFinanceStats(): FinanceStats {
        val transactions = getTransactions()
        val budgets = getBudgets()
        
        val income = transactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
        val expense = transactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
        val borrowed = transactions.filter { it.type == TransactionType.BORROWED && !it.isSettled }.sumOf { it.amount }
        val lent = transactions.filter { it.type == TransactionType.LENT && !it.isSettled }.sumOf { it.amount }
        
        return FinanceStats(
            totalIncome = income,
            totalExpense = expense,
            currentBalance = income - expense + borrowed - lent,
            totalBorrowed = borrowed,
            totalLent = lent,
            budgetStatus = budgets,
            recentTransactions = transactions.take(10)
        )
    }
}
