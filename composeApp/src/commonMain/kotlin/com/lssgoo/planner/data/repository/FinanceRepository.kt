package com.lssgoo.planner.data.repository

import com.lssgoo.planner.features.finance.models.*
import com.lssgoo.planner.util.Resource
import kotlinx.coroutines.flow.Flow

interface FinanceRepository {
    fun getTransactions(): Flow<Resource<List<Transaction>>>
    fun getBudgets(): Flow<Resource<List<Budget>>>
    fun getLogs(): Flow<Resource<List<FinanceLog>>>
    fun getFinanceStats(): Flow<Resource<FinanceStats>>

    suspend fun saveTransaction(transaction: Transaction): Resource<Boolean>
    suspend fun deleteTransaction(id: String): Resource<Boolean>
    
    suspend fun saveBudget(budget: Budget): Resource<Boolean>
    suspend fun deleteBudget(id: String): Resource<Boolean>
    
    suspend fun settleDebt(id: String): Resource<Boolean>
    
    fun generateTransactionsCSV(): Resource<String>
}
