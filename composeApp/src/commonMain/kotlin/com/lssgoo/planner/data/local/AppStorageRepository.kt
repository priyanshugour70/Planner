package com.lssgoo.planner.data.local

import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import com.lssgoo.planner.data.model.*
import com.lssgoo.planner.features.habits.models.*
import com.lssgoo.planner.util.KmpTimeUtils

/**
 * Multiplatform storage manager using multiplatform-settings and kotlinx-serialization.
 */
class AppStorageRepository(val settings: Settings) {

    private val json = Json { ignoreUnknownKeys = true; prettyPrint = false }

    companion object {
        private const val KEY_GOALS = "goals"
        private const val KEY_NOTES = "notes"
        private const val KEY_TASKS = "tasks"
        private const val KEY_EVENTS = "events"
        private const val KEY_HABITS = "habit_entries"
        private const val KEY_HABITS_LIST = "habits_list"
        private const val KEY_SETTINGS = "settings"
        private const val KEY_FIRST_LAUNCH = "first_launch"
        private const val KEY_LAST_SYNC = "last_sync"
        private const val KEY_USER_PROFILE = "user_profile"
        private const val KEY_REMINDERS = "reminders"
        private const val KEY_ONBOARDING_COMPLETE = "onboarding_complete"
        private const val KEY_JOURNAL_ENTRIES = "journal_entries"
        private const val KEY_JOURNAL_PROMPTS = "journal_prompts"
        private const val KEY_RECENT_SEARCHES = "recent_searches"
        private const val KEY_FINANCE_TRANSACTIONS = "finance_transactions"
        private const val KEY_FINANCE_BUDGETS = "finance_budgets"
        private const val KEY_FINANCE_LOGS = "finance_logs"
    }

    // ======================== GOALS ========================

    fun saveGoals(goals: List<Goal>) {
        val string = json.encodeToString(goals)
        settings[KEY_GOALS] = string
    }

    fun getGoals(): List<Goal> {
        val string = settings.getStringOrNull(KEY_GOALS) ?: return emptyList()
        return try {
            json.decodeFromString(string)
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun updateGoal(goal: Goal) {
        val goals = getGoals().toMutableList()
        val index = goals.indexOfFirst { it.id == goal.id }
        if (index != -1) {
            goals[index] = goal.copy(updatedAt = KmpTimeUtils.currentTimeMillis())
        } else {
            goals.add(goal)
        }
        saveGoals(goals)
    }

    fun deleteGoal(goalId: String) {
        val goals = getGoals().filter { it.id != goalId }
        saveGoals(goals)
    }

    // ======================== NOTES ========================

    fun saveNotes(notes: List<Note>) {
        val string = json.encodeToString(notes)
        settings[KEY_NOTES] = string
    }

    fun getNotes(): List<Note> {
        val string = settings.getStringOrNull(KEY_NOTES) ?: return emptyList()
        return try {
            json.decodeFromString(string)
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun addNote(note: Note) {
        val notes = getNotes().toMutableList()
        notes.add(0, note)
        saveNotes(notes)
    }

    fun updateNote(note: Note) {
        val notes = getNotes().toMutableList()
        val index = notes.indexOfFirst { it.id == note.id }
        if (index != -1) {
            notes[index] = note.copy(updatedAt = KmpTimeUtils.currentTimeMillis())
            saveNotes(notes)
        }
    }

    fun deleteNote(noteId: String) {
        val notes = getNotes().filter { it.id != noteId }
        saveNotes(notes)
    }

    // ======================== TASKS ========================

    fun saveTasks(tasks: List<Task>) {
        val string = json.encodeToString(tasks)
        settings[KEY_TASKS] = string
    }

    fun getTasks(): List<Task> {
        val string = settings.getStringOrNull(KEY_TASKS) ?: return emptyList()
        return try {
            json.decodeFromString(string)
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun addTask(task: Task) {
        val tasks = getTasks().toMutableList()
        tasks.add(0, task)
        saveTasks(tasks)
    }

    fun updateTask(task: Task) {
        val tasks = getTasks().toMutableList()
        val index = tasks.indexOfFirst { it.id == task.id }
        if (index != -1) {
            tasks[index] = task.copy(updatedAt = KmpTimeUtils.currentTimeMillis())
            saveTasks(tasks)
        }
    }

    fun deleteTask(taskId: String) {
        val tasks = getTasks().filter { it.id != taskId }
        saveTasks(tasks)
    }

    fun toggleTaskCompletion(taskId: String) {
        val tasks = getTasks().toMutableList()
        val index = tasks.indexOfFirst { it.id == taskId }
        if (index != -1) {
            val task = tasks[index]
            tasks[index] = task.copy(
                isCompleted = !task.isCompleted,
                completedAt = if (!task.isCompleted) KmpTimeUtils.currentTimeMillis() else null,
                updatedAt = KmpTimeUtils.currentTimeMillis()
            )
            saveTasks(tasks)
        }
    }

    // ======================== CALENDAR EVENTS ========================

    fun saveEvents(events: List<CalendarEvent>) {
        val string = json.encodeToString(events)
        settings[KEY_EVENTS] = string
    }

    fun getEvents(): List<CalendarEvent> {
        val string = settings.getStringOrNull(KEY_EVENTS) ?: return emptyList()
        return try {
            json.decodeFromString(string)
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun addEvent(event: CalendarEvent) {
        val events = getEvents().toMutableList()
        events.add(event)
        saveEvents(events)
    }

    fun updateEvent(event: CalendarEvent) {
        val events = getEvents().toMutableList()
        val index = events.indexOfFirst { it.id == event.id }
        if (index != -1) {
            events[index] = event
            saveEvents(events)
        }
    }

    fun deleteEvent(eventId: String) {
        val events = getEvents().filter { it.id != eventId }
        saveEvents(events)
    }

    fun getEventsForDate(date: Long): List<CalendarEvent> {
        val dayStart = KmpTimeUtils.getStartOfDay(date)
        val dayEnd = dayStart + 24 * 60 * 60 * 1000 - 1
        return getEvents().filter { it.date in dayStart..dayEnd }
    }

    // ======================== SETTINGS ========================

    fun saveSettings(appSettings: AppSettings) {
        val string = json.encodeToString(appSettings)
        settings[KEY_SETTINGS] = string
    }

    fun getSettings(): AppSettings {
        val string = settings.getStringOrNull(KEY_SETTINGS) ?: return AppSettings()
        return try {
            json.decodeFromString(string)
        } catch (e: Exception) {
            AppSettings()
        }
    }

    // ======================== USER PROFILE ========================

    fun saveUserProfile(profile: UserProfile) {
        val string = json.encodeToString(profile)
        settings[KEY_USER_PROFILE] = string
    }

    fun getUserProfile(): UserProfile? {
        val string = settings.getStringOrNull(KEY_USER_PROFILE) ?: return null
        return try {
            json.decodeFromString(string)
        } catch (e: Exception) {
            null
        }
    }

    fun isOnboardingComplete(): Boolean {
        return settings.getBoolean(KEY_ONBOARDING_COMPLETE, false)
    }

    fun setOnboardingComplete() {
        settings[KEY_ONBOARDING_COMPLETE] = true
    }

    // ======================== REMINDERS ========================

    fun saveReminders(reminders: List<Reminder>) {
        val string = json.encodeToString(reminders)
        settings[KEY_REMINDERS] = string
    }

    fun getReminders(): List<Reminder> {
        val string = settings.getStringOrNull(KEY_REMINDERS) ?: return emptyList()
        return try {
            json.decodeFromString(string)
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun addReminder(reminder: Reminder) {
        val reminders = getReminders().toMutableList()
        reminders.add(0, reminder)
        saveReminders(reminders)
    }

    fun updateReminder(reminder: Reminder) {
        val reminders = getReminders().toMutableList()
        val index = reminders.indexOfFirst { it.id == reminder.id }
        if (index != -1) {
            reminders[index] = reminder.copy(updatedAt = KmpTimeUtils.currentTimeMillis())
            saveReminders(reminders)
        }
    }

    fun deleteReminder(reminderId: String) {
        val reminders = getReminders().filter { it.id != reminderId }
        saveReminders(reminders)
    }

    fun toggleReminderEnabled(reminderId: String) {
        val reminders = getReminders().toMutableList()
        val index = reminders.indexOfFirst { it.id == reminderId }
        if (index != -1) {
            val reminder = reminders[index]
            reminders[index] = reminder.copy(
                isEnabled = !reminder.isEnabled,
                updatedAt = KmpTimeUtils.currentTimeMillis()
            )
            saveReminders(reminders)
        }
    }

    // ======================== FIRST LAUNCH ========================

    fun isFirstLaunch(): Boolean {
        return settings.getBoolean(KEY_FIRST_LAUNCH, true)
    }

    fun setFirstLaunchComplete() {
        settings[KEY_FIRST_LAUNCH] = false
    }

    // ======================== BACKUP & RESTORE ========================
    // Note: Export/Import logic simplified for string based backup

    fun exportAllData(): String {
        val appData = AppData(
            version = 3,
            exportedAt = KmpTimeUtils.currentTimeMillis(),
            goals = getGoals(),
            notes = getNotes(),
            tasks = getTasks(),
            events = getEvents(),
            reminders = getReminders(),
            habitEntries = getHabitEntries(),
            habits = getHabits(),
            journalEntries = getJournalEntries(),
            transactions = getTransactions(),
            budgets = getBudgets(),
            logs = getFinanceLogs(),
            userProfile = getUserProfile(),
            settings = getSettings()
        )
        return json.encodeToString(appData)
    }

    fun importAllData(jsonData: String): Boolean {
        return try {
            val appData = json.decodeFromString<AppData>(jsonData)
            saveGoals(appData.goals)
            saveNotes(appData.notes)
            saveTasks(appData.tasks)
            saveEvents(appData.events)
            
            appData.reminders?.let { saveReminders(it) }
            appData.habits?.let { saveHabits(it) }
            appData.journalEntries?.let { saveJournalEntries(it) }
            
            appData.transactions?.let { saveTransactions(it) }
            appData.budgets?.let { saveBudgets(it) }
            appData.logs?.let { saveFinanceLogs(it) }
            
            saveHabitEntries(appData.habitEntries)
            appData.userProfile?.let { saveUserProfile(it) }
            saveSettings(appData.settings)
            
            settings.putLong(KEY_LAST_SYNC, KmpTimeUtils.currentTimeMillis())
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun clearAllData() {
        settings.clear()
    }

    // ======================== HABITS ========================
    fun saveHabits(habits: List<Habit>) {
        val string = json.encodeToString(habits)
        settings[KEY_HABITS_LIST] = string
    }

    fun getHabits(): List<Habit> {
        val string = settings.getStringOrNull(KEY_HABITS_LIST) ?: return emptyList()
        return try {
            json.decodeFromString(string)
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun addHabit(habit: Habit) {
        val habits = getHabits().toMutableList()
        habits.add(habit)
        saveHabits(habits)
    }

    fun updateHabit(habit: Habit) {
        val habits = getHabits().toMutableList()
        val index = habits.indexOfFirst { it.id == habit.id }
        if (index != -1) {
            habits[index] = habit
        } else {
            habits.add(habit)
        }
        saveHabits(habits)
    }

    fun deleteHabit(habitId: String) {
        val habits = getHabits().filter { it.id != habitId }
        saveHabits(habits)
    }

    // ======================== HABIT ENTRIES ========================

    fun saveHabitEntries(entries: List<HabitEntry>) {
        val string = json.encodeToString(entries)
        settings[KEY_HABITS] = string
    }

    fun getHabitEntries(): List<HabitEntry> {
        val string = settings.getStringOrNull(KEY_HABITS) ?: return emptyList()
        return try {
            json.decodeFromString(string)
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun addHabitEntry(entry: HabitEntry) {
        val entries = getHabitEntries().toMutableList()
        val dayStart = KmpTimeUtils.getStartOfDay(entry.date)
        entries.removeAll { 
            KmpTimeUtils.getStartOfDay(it.date) == dayStart && it.habitId == entry.habitId 
        }
        entries.add(entry)
        saveHabitEntries(entries)
    }

    fun updateHabitEntry(entry: HabitEntry) {
        val entries = getHabitEntries().toMutableList()
        val index = entries.indexOfFirst { it.id == entry.id }
        if (index != -1) {
            entries[index] = entry
        } else {
            entries.add(entry)
        }
        saveHabitEntries(entries)
    }

    fun deleteHabitEntry(entryId: String) {
        val entries = getHabitEntries().toMutableList()
        val entry = entries.find { it.id == entryId }
        if (entry != null) {
            entries.remove(entry)
            saveHabitEntries(entries)
        }
    }

    fun getHabitEntries(habitId: String): List<HabitEntry> {
        return getHabitEntries().filter { it.habitId == habitId }
    }

    fun getHabitEntriesForDate(date: Long): List<HabitEntry> {
        val start = KmpTimeUtils.getStartOfDay(date)
        return getHabitEntries().filter { KmpTimeUtils.getStartOfDay(it.date) == start }
    }

    // ======================== JOURNAL ========================

    fun saveJournalEntries(entries: List<JournalEntry>) {
        val string = json.encodeToString(entries)
        settings[KEY_JOURNAL_ENTRIES] = string
    }

    fun getJournalEntries(): List<JournalEntry> {
        val string = settings.getStringOrNull(KEY_JOURNAL_ENTRIES) ?: return emptyList()
        return try {
            json.decodeFromString(string)
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun addJournalEntry(entry: JournalEntry) {
        val entries = getJournalEntries().toMutableList()
        entries.add(0, entry)
        saveJournalEntries(entries)
    }

    fun updateJournalEntry(entry: JournalEntry) {
        val entries = getJournalEntries().toMutableList()
        val index = entries.indexOfFirst { it.id == entry.id }
        if (index != -1) {
            entries[index] = entry.copy(updatedAt = KmpTimeUtils.currentTimeMillis())
        } else {
            entries.add(entry)
        }
        saveJournalEntries(entries)
    }

    fun deleteJournalEntry(entryId: String) {
        val entries = getJournalEntries().filter { it.id != entryId }
        saveJournalEntries(entries)
    }

    fun saveJournalPrompts(prompts: List<JournalPrompt>) {
        val string = json.encodeToString(prompts)
        settings[KEY_JOURNAL_PROMPTS] = string
    }

    fun getJournalPrompts(): List<JournalPrompt> {
        val string = settings.getStringOrNull(KEY_JOURNAL_PROMPTS) ?: return emptyList()
        return try {
            json.decodeFromString(string)
        } catch (e: Exception) {
            emptyList()
        }
    }

    // ======================== RECENT SEARCHES ========================

    fun saveRecentSearches(searches: List<String>) {
        val string = json.encodeToString(searches)
        settings[KEY_RECENT_SEARCHES] = string
    }

    fun getRecentSearches(): List<String> {
        val string = settings.getStringOrNull(KEY_RECENT_SEARCHES) ?: return emptyList()
        return try {
            json.decodeFromString(string)
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun addRecentSearch(query: String) {
        val searches = getRecentSearches().toMutableList()
        searches.remove(query)
        searches.add(0, query)
        saveRecentSearches(searches.take(10))
    }

    // ======================== FINANCE ========================

    fun saveTransactions(transactions: List<Transaction>) {
        val string = json.encodeToString(transactions)
        settings[KEY_FINANCE_TRANSACTIONS] = string
    }

    fun getTransactions(): List<Transaction> {
        val string = settings.getStringOrNull(KEY_FINANCE_TRANSACTIONS) ?: return emptyList()
        return try {
            json.decodeFromString(string)
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun addTransaction(transaction: Transaction) {
        val list = getTransactions().toMutableList()
        list.add(0, transaction)
        saveTransactions(list)
        logFinanceAction("ADD", "TRANSACTION", transaction.id, "Added ${transaction.type} of ${transaction.amount} in ${transaction.category}")

        if (transaction.type == TransactionType.EXPENSE) {
            updateBudgetSpending(transaction.category, transaction.amount)
        }
    }

    fun updateTransaction(transaction: Transaction) {
        val list = getTransactions().toMutableList()
        val index = list.indexOfFirst { it.id == transaction.id }
        if (index != -1) {
            val old = list[index]
            list[index] = transaction
            saveTransactions(list)
            logFinanceAction("UPDATE", "TRANSACTION", transaction.id, "Updated transaction from ${old.amount} to ${transaction.amount}")
        }
    }

    fun deleteTransaction(id: String) {
        val list = getTransactions().toMutableList()
        val transaction = list.find { it.id == id }
        if (transaction != null) {
            list.remove(transaction)
            saveTransactions(list)
            logFinanceAction("REMOVE", "TRANSACTION", id, "Removed transaction of ${transaction.amount}")

            if (transaction.type == TransactionType.EXPENSE) {
                updateBudgetSpending(transaction.category, -transaction.amount)
            }
        }
    }

    // Budgets
    fun saveBudgets(budgets: List<Budget>) {
        val string = json.encodeToString(budgets)
        settings[KEY_FINANCE_BUDGETS] = string
    }

    fun getBudgets(): List<Budget> {
        val string = settings.getStringOrNull(KEY_FINANCE_BUDGETS) ?: return emptyList()
        return try {
            json.decodeFromString(string)
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun addBudget(budget: Budget) {
        val list = getBudgets().toMutableList()
        list.add(budget)
        saveBudgets(list)
        logFinanceAction("ADD", "BUDGET", budget.id, "Added budget for ${budget.category ?: "Overall"} with limit ${budget.limitAmount}")
    }

    fun removeBudget(id: String) {
        val list = getBudgets().toMutableList()
        val budget = list.find { it.id == id }
        if (budget != null) {
            list.remove(budget)
            saveBudgets(list)
            logFinanceAction("REMOVE", "BUDGET", id, "Removed budget for ${budget.category ?: "Overall"}")
        }
    }

    private fun updateBudgetSpending(category: TransactionCategory, amount: Double) {
        val budgets = getBudgets().toMutableList()
        var changed = false
        for (i in budgets.indices) {
            if (budgets[i].category == category || budgets[i].category == null) {
                budgets[i] = budgets[i].copy(spentAmount = budgets[i].spentAmount + amount)
                changed = true
            }
        }
        if (changed) saveBudgets(budgets)
    }

    // Logs
    fun saveFinanceLogs(logs: List<FinanceLog>) {
        val string = json.encodeToString(logs)
        settings[KEY_FINANCE_LOGS] = string
    }

    fun getFinanceLogs(): List<FinanceLog> {
        val string = settings.getStringOrNull(KEY_FINANCE_LOGS) ?: return emptyList()
        return try {
            json.decodeFromString(string)
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun logFinanceAction(action: String, entityType: String, entityId: String, description: String) {
        val logs = getFinanceLogs().toMutableList()
        logs.add(0, FinanceLog(
            action = action,
            entityType = entityType,
            description = description
        ))
        saveFinanceLogs(logs.take(1000))
    }
}
