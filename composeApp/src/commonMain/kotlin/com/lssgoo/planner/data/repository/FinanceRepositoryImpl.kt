package com.lssgoo.planner.data.repository

import com.lssgoo.planner.data.local.AppStorageRepository
import com.lssgoo.planner.features.finance.models.*
import com.lssgoo.planner.util.Resource
import com.lssgoo.planner.util.KmpTimeUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.*

/**
 * Implementation of FinanceRepository using local AppStorageRepository
 */
class FinanceRepositoryImpl(
    private val storage: AppStorageRepository
) : FinanceRepository {

    override fun getTransactions(): Flow<Resource<List<Transaction>>> = flow {
        emit(Resource.Loading)
        try {
            val transactions = storage.getTransactions().filter { !it.isDeleted }
            emit(Resource.Success(transactions))
        } catch (e: Exception) {
            emit(Resource.Error(e))
        }
    }

    override fun getBudgets(): Flow<Resource<List<Budget>>> = flow {
        emit(Resource.Loading)
        try {
            val budgets = storage.getBudgets().filter { !it.isDeleted }
            emit(Resource.Success(budgets))
        } catch (e: Exception) {
            emit(Resource.Error(e))
        }
    }

    override fun getLogs(): Flow<Resource<List<FinanceLog>>> = flow {
        emit(Resource.Loading)
        try {
            val logs = storage.getFinanceLogs()
            emit(Resource.Success(logs))
        } catch (e: Exception) {
            emit(Resource.Error(e))
        }
    }

    override fun getFinanceStats(): Flow<Resource<FinanceStats>> = flow {
        emit(Resource.Loading)
        try {
            val transactions = storage.getTransactions().filter { !it.isDeleted }
            val budgets = storage.getBudgets().filter { !it.isDeleted }

            val income = transactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
            val expense = transactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
            val borrowed = transactions.filter { it.type == TransactionType.BORROWED && !it.isSettled }.sumOf { it.amount }
            val lent = transactions.filter { it.type == TransactionType.LENT && !it.isSettled }.sumOf { it.amount }
            
            val categoryMap = transactions.filter { it.type == TransactionType.EXPENSE }
                .groupBy { it.category }
                .mapValues { it.value.sumOf { t -> t.amount } }

            val today = KmpTimeUtils.getStartOfDay(KmpTimeUtils.currentTimeMillis())
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

            val recurring = transactions.filter { it.isRecurring }

            val stats = FinanceStats(
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
            emit(Resource.Success(stats))
        } catch (e: Exception) {
            emit(Resource.Error(e))
        }
    }

    override suspend fun saveTransaction(transaction: Transaction): Resource<Boolean> {
        return try {
            if (transaction.id.isNotEmpty() && storage.getTransactions().any { it.id == transaction.id }) {
                storage.updateTransaction(transaction)
            } else {
                storage.addTransaction(transaction)
            }
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override suspend fun deleteTransaction(id: String): Resource<Boolean> {
        return try {
            val t = storage.getTransactions().find { it.id == id }
            if (t != null) {
                storage.updateTransaction(t.copy(isDeleted = true))
                // Note: updating AppStorageRepository's 'deleteTransaction' might physically remove it.
                // But here we are soft deleting. 
                // AppStorageRepository.kt 'deleteTransaction' removes it from list. 
                // We should use updateTransaction to mark as deleted instead of calling deleteTransaction.
            }
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override suspend fun saveBudget(budget: Budget): Resource<Boolean> {
        return try {
            // AppStorageRepository doesn't have updateBudget logic clearly exposed usually, let's check.
            // It has removeBudget and addBudget. 
            // We can treat it like that for now or update directly if we could.
            // For now, if ID exists, remove then add. 
            val existing = storage.getBudgets().find { it.id == budget.id }
            if (existing != null) {
                storage.removeBudget(existing.id)
            }
            storage.addBudget(budget)
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override suspend fun deleteBudget(id: String): Resource<Boolean> {
        return try {
            // Soft delete
             val existing = storage.getBudgets().find { it.id == id }
             if (existing != null) {
                 storage.removeBudget(id) // Remove old
                 storage.addBudget(existing.copy(isDeleted = true)) // Add marked as deleted
             }
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override suspend fun settleDebt(id: String): Resource<Boolean> {
        return try {
            // The original logic was in the repo class. We can replicate it here via storage calls.
            val t = storage.getTransactions().find { it.id == id }
            if (t != null) {
                val updated = t.copy(isSettled = true)
                storage.updateTransaction(updated)
                
                 val logs = storage.getFinanceLogs().toMutableList()
                 logs.add(0, FinanceLog(
                    action = "SETTLED",
                    entityType = "TRANSACTION",
                    description = "Settled ${t.type} of ${t.amount} with ${t.personName ?: "someone"}"
                 ))
                 storage.saveFinanceLogs(logs)
            }
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override fun generateTransactionsCSV(): Resource<String> {
        return try {
            val transactions = storage.getTransactions().filter { !it.isDeleted }
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
            Resource.Success(sb.toString())
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    private fun formatDateTime(timestamp: Long): String {
        val instant = Instant.fromEpochMilliseconds(timestamp)
        val dateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
        val y = dateTime.year
        val m = dateTime.monthNumber.toString().padStart(2, '0')
        val d = dateTime.dayOfMonth.toString().padStart(2, '0')
        val h = dateTime.hour.toString().padStart(2, '0')
        val min = dateTime.minute.toString().padStart(2, '0')
        return "$y-$m-$d $h:$min"
    }
}
