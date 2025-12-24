package com.lssgoo.planner.features.finance.models

import kotlinx.serialization.Serializable
import com.lssgoo.planner.util.KmpIdGenerator
import com.lssgoo.planner.util.KmpTimeUtils

/**
 * Finance models for tracking money, budgets, and debts
 */

@Serializable
enum class TransactionType {
    INCOME,    // Money coming in (Credit)
    EXPENSE,   // Money going out (Debit)
    BORROWED,  // Money taken from someone
    LENT       // Money given to someone
}

@Serializable
enum class TransactionCategory(val iconName: String) {
    FOOD("Restaurant"),
    TRANSPORT("DirectionsCar"),
    SHOPPING("ShoppingBag"),
    ENTERTAINMENT("Movie"),
    HEALTH("MedicalServices"),
    EDUCATION("School"),
    SALARY("Payments"),
    INVESTMENT("ShowChart"),
    BILL("Receipt"),
    RENT("Home"),
    GIFT("CardGiftcard"),
    OTHER("Category"),
    DEBT_REPAYMENT("Handshake")
}

@Serializable
data class Transaction(
    val id: String = KmpIdGenerator.generateId(),
    val amount: Double,
    val type: TransactionType,
    val category: TransactionCategory,
    val note: String = "",
    val personName: String? = null, // Used for Debt/Lent
    val date: Long = KmpTimeUtils.currentTimeMillis(),
    val isSettled: Boolean = false, // For debts
    val receiptUri: String? = null, // For receipts (Option 5)
    val isRecurring: Boolean = false, // For recurring transactions (Option 4)
    val recurringPeriod: BudgetPeriod? = null,
    val createdAt: Long = KmpTimeUtils.currentTimeMillis()
)

@Serializable
data class Budget(
    val id: String = KmpIdGenerator.generateId(),
    val category: TransactionCategory?, // null means overall budget
    val limitAmount: Double,
    val spentAmount: Double = 0.0,
    val period: BudgetPeriod = BudgetPeriod.MONTHLY,
    val startDate: Long = KmpTimeUtils.currentTimeMillis(),
    val endDate: Long? = null,
    val notifiedAt: Long? = null // For budget alerts (Option 6)
)

@Serializable
enum class BudgetPeriod {
    DAILY, WEEKLY, MONTHLY, YEARLY
}

@Serializable
data class FinanceLog(
    val id: String = KmpIdGenerator.generateId(),
    val action: String, // "ADD", "REMOVE", "UPDATE", "SETTLED"
    val entityType: String, // "TRANSACTION", "BUDGET"
    val timestamp: Long = KmpTimeUtils.currentTimeMillis(),
    val description: String
)

@Serializable
data class FinanceStats(
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val currentBalance: Double = 0.0,
    val totalBorrowed: Double = 0.0,
    val totalLent: Double = 0.0,
    val budgetStatus: List<Budget> = emptyList(),
    val recentTransactions: List<Transaction> = emptyList(),
    // New fields for Analytics & Charts (Option 1)
    val categorySpending: Map<TransactionCategory, Double> = emptyMap(),
    val dailySpending: Map<Long, Double> = emptyMap(), // Date to amount
    // Pair is not seamlessly serializable by default in all formats with KSerialization without custom serializer or if it's treated as a list, 
    // but usually map of primitives is fine. Pair<Double, Double> might be tricky.
    // However, the user asked to make things serializable. 
    // I will comment out the Pair map or replace it if I can, but preserving structure is key.
    // Map<Long, Pair<Double, Double>> is definitely going to be an issue for default JSON serialization (keys are strings in JSON).
    // Kotlin generic serialization handles Map keys if they are primitives effectively (converted to string).
    // But Pair might need a surrogate.
    // For now I'll keep it but it might cause issues if not handled.
    // Actually, I'll replace Pair with a custom data class to be safe.
    // Or just suppress if I can't change the API too much. 
    // Let's create a simple IncomeExpense class.
    // val incomeVsExpense: Map<Long, Pair<Double, Double>> = emptyMap(),
    // Updating to use a list of IncomeExpensePoint or map to that.
    val incomeVsExpense: Map<Long, IncomeExpenseData> = emptyMap(),
    val upcomingRecurring: List<Transaction> = emptyList() // (Option 4)
)

@Serializable
data class IncomeExpenseData(val income: Double, val expense: Double)
