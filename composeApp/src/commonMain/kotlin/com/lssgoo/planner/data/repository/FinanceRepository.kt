package com.lssgoo.planner.data.repository

import com.lssgoo.planner.data.local.AppStorageRepository
import com.lssgoo.planner.data.model.*
import com.lssgoo.planner.features.finance.models.IncomeExpenseData
import com.lssgoo.planner.util.KmpTimeUtils
import kotlinx.datetime.*

/**
 * Repository for managing Finance data - logic for charts, debts, and reports
 */
class FinanceRepository(private val storage: AppStorageRepository) {

    fun getTransactions(): List<Transaction> = storage.getTransactions()
    fun getBudgets(): List<Budget> = storage.getBudgets()
    fun getLogs(): List<FinanceLog> = storage.getFinanceLogs()

    fun addTransaction(transaction: Transaction) {
        storage.addTransaction(transaction)
    }

    fun updateTransaction(transaction: Transaction) {
        storage.updateTransaction(transaction)
    }

    fun deleteTransaction(id: String) {
        storage.deleteTransaction(id)
    }

    fun addBudget(budget: Budget) {
        storage.addBudget(budget)
    }

    fun removeBudget(id: String) {
        storage.removeBudget(id)
    }

    /**
     * Settle a debt (Option 2)
     */
    fun settleDebt(id: String) {
        val transactions = getTransactions().toMutableList()
        val index = transactions.indexOfFirst { it.id == id }
        if (index != -1) {
            val debt = transactions[index]
            transactions[index] = debt.copy(isSettled = true)
            storage.saveTransactions(transactions)
            
            // Add a settlement log
            val logs = storage.getFinanceLogs().toMutableList()
            logs.add(0, FinanceLog(
                action = "SETTLED",
                entityType = "TRANSACTION",
                description = "Settled ${debt.type} of ${debt.amount} with ${debt.personName ?: "someone"}"
            ))
            storage.saveFinanceLogs(logs)
        }
    }

    fun getFinanceStats(): FinanceStats {
        val transactions = getTransactions()
        val budgets = getBudgets()
        
        val income = transactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
        val expense = transactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
        val borrowed = transactions.filter { it.type == TransactionType.BORROWED && !it.isSettled }.sumOf { it.amount }
        val lent = transactions.filter { it.type == TransactionType.LENT && !it.isSettled }.sumOf { it.amount }
        
        // Option 1: Analytics Data
        val categoryMap = transactions.filter { it.type == TransactionType.EXPENSE }
            .groupBy { it.category }
            .mapValues { it.value.sumOf { t -> t.amount } }

        val today = KmpTimeUtils.getStartOfDay(KmpTimeUtils.currentTimeMillis())
        
        // Last 30 days
        val thirtyDaysAgo = today - (30L * 24 * 60 * 60 * 1000)
        
        val dailyMap = transactions.filter { it.type == TransactionType.EXPENSE && it.date >= thirtyDaysAgo }
            .groupBy { KmpTimeUtils.getStartOfDay(it.date) }
            .mapValues { it.value.sumOf { t -> t.amount } }

        val trendMap = transactions.filter { it.date >= thirtyDaysAgo }
            .groupBy { KmpTimeUtils.getStartOfDay(it.date) }
            .mapValues { (_, list) ->
                val inc = list.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
                val exp = list.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
                IncomeExpenseData(income = inc, expense = exp)
            }

        // Option 4: Recurring entries
        val recurring = transactions.filter { it.isRecurring }

        return FinanceStats(
            totalIncome = income,
            totalExpense = expense,
            currentBalance = income - expense + borrowed - lent,
            totalBorrowed = borrowed,
            totalLent = lent,
            budgetStatus = budgets,
            recentTransactions = transactions.take(10),
            categorySpending = categoryMap,
            dailySpending = dailyMap,
            incomeVsExpense = trendMap,
            upcomingRecurring = recurring
        )
    }

    /**
     * CSV Export logic (Option 7)
     */
    fun generateTransactionsCSV(): String {
        val transactions = getTransactions()
        val sb = StringBuilder()
        sb.append("ID,Date,Amount,Type,Category,Note,Person,IsSettled\n")
        
        transactions.forEach { t ->
            val dateStr = formatDateTime(t.date)
            sb.append("${t.id},")
            sb.append("${dateStr},")
            sb.append("${t.amount},")
            sb.append("${t.type},")
            sb.append("${t.category},")
            sb.append("\"${t.note}\",")
            sb.append("${t.personName ?: ""},")
            sb.append("${t.isSettled}\n")
        }
        return sb.toString()
    }

    private fun formatDateTime(timestamp: Long): String {
        val instant = Instant.fromEpochMilliseconds(timestamp)
        val dateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
        // Simple YYYY-MM-DD HH:MM format manually
        val y = dateTime.year
        val m = dateTime.monthNumber.toString().padStart(2, '0')
        val d = dateTime.dayOfMonth.toString().padStart(2, '0')
        val h = dateTime.hour.toString().padStart(2, '0')
        val min = dateTime.minute.toString().padStart(2, '0')
        return "$y-$m-$d $h:$min"
    }
}
