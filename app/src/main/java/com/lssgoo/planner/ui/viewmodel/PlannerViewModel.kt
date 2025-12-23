package com.lssgoo.planner.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.viewModelScope
import com.lssgoo.planner.data.local.LocalStorageManager
import com.lssgoo.planner.data.model.*
import com.lssgoo.planner.features.habits.models.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Root ViewModel for global app state
 */
class PlannerViewModel(application: Application) : BaseViewModel(application) {
    
    private val storageManager = LocalStorageManager(application)
    
    private val syncRepository = com.lssgoo.planner.data.repository.SyncRepository(application)
    
    private val _settings = MutableStateFlow(storageManager.getSettings())
    val settings: StateFlow<AppSettings> = _settings.asStateFlow()
    
    private val _userProfile = MutableStateFlow(storageManager.getUserProfile() ?: UserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()
    
    private val _isOnboardingComplete = MutableStateFlow(storageManager.isOnboardingComplete())
    val isOnboardingComplete: StateFlow<Boolean> = _isOnboardingComplete.asStateFlow()

    private val _isCheckingSync = MutableStateFlow(false)
    val isCheckingSync: StateFlow<Boolean> = _isCheckingSync.asStateFlow()

    private val financeRepository = com.lssgoo.planner.data.repository.FinanceRepository(storageManager)
    
    // Feature data states
    val goals = MutableStateFlow<List<Goal>>(emptyList())
    val tasks = MutableStateFlow<List<Task>>(emptyList())
    val notes = MutableStateFlow<List<Note>>(emptyList())
    val reminders = MutableStateFlow<List<Reminder>>(emptyList())
    // journalEntries declared below with logic
    val habits = MutableStateFlow<List<Habit>>(emptyList())
    val transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val financeLogs = MutableStateFlow<List<FinanceLog>>(emptyList())
    val budgets = MutableStateFlow<List<Budget>>(emptyList())
    val dashboardStats = MutableStateFlow(DashboardStats())
    val financeStats = MutableStateFlow(FinanceStats())
    val analyticsData = MutableStateFlow<AnalyticsData?>(null)
    val searchQuery = MutableStateFlow("")
    val searchResults = MutableStateFlow<List<SearchResult>>(emptyList())
    val recentSearches = MutableStateFlow<List<String>>(emptyList())
    val searchFilters = MutableStateFlow(SearchFilters())
    val selectedDate = MutableStateFlow(System.currentTimeMillis())
    val events = MutableStateFlow<List<CalendarEvent>>(emptyList())

    // ======================== JOURNAL ========================
    private val _journalEntries = MutableStateFlow<List<JournalEntry>>(emptyList())
    val journalEntries: StateFlow<List<JournalEntry>> = _journalEntries.asStateFlow()
    
    private val _journalPrompts = MutableStateFlow<List<JournalPrompt>>(emptyList())
    val journalPrompts: StateFlow<List<JournalPrompt>> = _journalPrompts.asStateFlow()

    init {
        // Launch data loading in parallel safely
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            checkCloudBackup()
        }
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            loadFinanceData()
        }
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            loadHabits()
        }
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            loadJournalData()
        }
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            loadGoals()
        }
    }

    private suspend fun checkCloudBackup() {
        try {
            if (!_isOnboardingComplete.value) {
                _isCheckingSync.value = true
                val restored = syncRepository.checkAndDownloadBackup()
                if (restored) {
                    _userProfile.value = storageManager.getUserProfile() ?: UserProfile()
                    _isOnboardingComplete.value = storageManager.isOnboardingComplete()
                    _settings.value = storageManager.getSettings()
                }
                _isCheckingSync.value = false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            _isCheckingSync.value = false
        }
    }
    
    fun saveUserProfile(profile: UserProfile) {
        viewModelScope.launch {
            storageManager.saveUserProfile(profile)
            _userProfile.value = profile
            syncToCloud()
        }
    }
    
    fun setOnboardingComplete(v: Boolean = true) {
        viewModelScope.launch {
            storageManager.setOnboardingComplete()
            _isOnboardingComplete.value = true
            syncToCloud()
        }
    }
    
    fun updateSettings(newSettings: AppSettings) {
        viewModelScope.launch {
            storageManager.saveSettings(newSettings)
            _settings.value = newSettings
            syncToCloud()
        }
    }
    
    fun updateUserProfile(profile: UserProfile) {
        saveUserProfile(profile)
    }
    
    fun clearAllData() {
        viewModelScope.launch {
            storageManager.clearAllData()
            _userProfile.value = UserProfile()
            _isOnboardingComplete.value = false
        }
    }
    
    val lastSyncTime = MutableStateFlow(System.currentTimeMillis())
    
    fun syncToCloud() {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            try {
                // If BaseViewModel has _isSyncing, we can use it if visible, or just ignore for now to avoid errors
                syncRepository.syncToCloud()
                lastSyncTime.value = System.currentTimeMillis()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    fun syncFromCloud() {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            _isCheckingSync.value = true
            try {
                val restored = syncRepository.checkAndDownloadBackup()
                if (restored) {
                    _userProfile.value = storageManager.getUserProfile() ?: UserProfile()
                    _isOnboardingComplete.value = storageManager.isOnboardingComplete()
                    _settings.value = storageManager.getSettings()
                    showSnackbar("Data restored!")
                } else {
                    showSnackbar("No cloud backup found")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            _isCheckingSync.value = false
        }
    }
    
    fun exportDataToFile(context: Context): android.net.Uri? {
        val json = storageManager.exportAllData()
        // Simple file export implementation or null
        return null 
    }
    
    fun importData(json: String): Boolean {
         val success = storageManager.importAllData(json)
         if(success) {
             // Refresh data
             loadFinanceData()
             loadGoals()
             loadHabits()
             loadJournalData()
         }
         return success
    }
    
    fun initializeAutoSync() {}

    private fun loadFinanceData() {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            try {
                transactions.value = financeRepository.getTransactions()
                val currentBudgets = financeRepository.getBudgets()
                if (currentBudgets.isEmpty()) {
                     ensureDefaultBudgets()
                     budgets.value = financeRepository.getBudgets()
                } else {
                     budgets.value = currentBudgets
                }
                financeLogs.value = financeRepository.getLogs()
                financeStats.value = financeRepository.getFinanceStats()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun loadHabits() {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            try {
                val loaded = storageManager.getHabits()
                ensureDefaultHabits(loaded)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    private fun loadJournalData() {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            try {
                _journalEntries.value = storageManager.getJournalEntries()
                val prompts = storageManager.getJournalPrompts()
                if (prompts.isEmpty()) {
                    initializeDefaultPrompts()
                } else {
                    _journalPrompts.value = prompts
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    private fun loadGoals() {
         viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            try {
                val loaded = storageManager.getGoals()
                if (loaded.isEmpty()) {
                    ensureDefaultGoals()
                    goals.value = storageManager.getGoals()
                } else {
                    goals.value = loaded
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    // DEFAULT DATA GENERATORS
    
    private fun ensureDefaultBudgets() {
        val defaults = listOf(
            Budget(category = TransactionCategory.FOOD, limitAmount = 500.0, spentAmount = 0.0),
            Budget(category = TransactionCategory.TRANSPORT, limitAmount = 300.0, spentAmount = 0.0),
            Budget(category = TransactionCategory.ENTERTAINMENT, limitAmount = 200.0, spentAmount = 0.0),
            Budget(category = TransactionCategory.SHOPPING, limitAmount = 400.0, spentAmount = 0.0)
        )
        defaults.forEach { financeRepository.addBudget(it) }
    }
    
    private fun ensureDefaultGoals() {
        // Use the pre-defined default goals compatible with current Model
        val defaults = DefaultGoals.goals
        defaults.forEach { storageManager.updateGoal(it) }
    }

    private fun ensureDefaultHabits(currentHabits: List<Habit>) {
        val defaults = listOf(
            Habit(title = "Drink Water", description = "Stay hydrated with 8 glasses a day", icon = "💧", iconColor = 0xFF2196F3, type = HabitType.QUANTITATIVE, targetValue = 8f, unit = "glasses", timeOfDay = HabitTimeOfDay.ANY_TIME, goalId = null),
            Habit(title = "Read Books", description = "Read at least 20 pages", icon = "📚", iconColor = 0xFF9C27B0, type = HabitType.QUANTITATIVE, targetValue = 20f, unit = "pages", timeOfDay = HabitTimeOfDay.EVENING, goalId = null),
            Habit(title = "Morning Workout", description = "Start the day with energy", icon = "💪", iconColor = 0xFFF44336, type = HabitType.YES_NO, timeOfDay = HabitTimeOfDay.MORNING, goalId = null),
            Habit(title = "Meditation", description = "Mindfulness session", icon = "🧘", iconColor = 0xFF4CAF50, type = HabitType.TIMER, targetValue = 10f, unit = "mins", timeOfDay = HabitTimeOfDay.MORNING, goalId = null),
            Habit(title = "Journaling", description = "Reflect on the day", icon = "✍️", iconColor = 0xFFFFC107, type = HabitType.YES_NO, timeOfDay = HabitTimeOfDay.EVENING, goalId = null)
        )
        
        val missingDefaults = defaults.filter { default -> 
            currentHabits.none { it.title == default.title } 
        }
        
        if (missingDefaults.isNotEmpty()) {
            missingDefaults.forEach { storageManager.addHabit(it) }
            habits.value = currentHabits + missingDefaults
        } else {
            habits.value = currentHabits
        }
    }

    // JOURNAL LOGIC
    private fun initializeDefaultPrompts() {
        val defaults = listOf(
            JournalPrompt(text = "What is one thing that made you smile today?", category = PromptCategory.GRATITUDE),
            JournalPrompt(text = "What challenge did you overcome recently?", category = PromptCategory.REFLECTION),
            JournalPrompt(text = "What form of self-care did you practice today?", category = PromptCategory.SELF_IMPROVEMENT),
            JournalPrompt(text = "What is a goal you want to focus on this week?", category = PromptCategory.GOAL_REVIEW),
            JournalPrompt(text = "Who are you grateful for in your life right now?", category = PromptCategory.GRATITUDE),
            JournalPrompt(text = "What did you learn today?", category = PromptCategory.SELF_IMPROVEMENT)
        )
        storageManager.saveJournalPrompts(defaults)
        _journalPrompts.value = defaults
    }
    
    fun addJournalEntry(entry: JournalEntry) {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            try {
                storageManager.addJournalEntry(entry)
                loadJournalData()
                syncToCloud()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getJournalEntryForDate(d: Long): JournalEntry? {
        val dayStart = getStartOfDay(d)
        val dayEnd = dayStart + 24 * 60 * 60 * 1000 - 1
        return _journalEntries.value.find { it.date in dayStart..dayEnd }
    }
    
    fun getDailyPrompt(): JournalPrompt? {
        val prompts = _journalPrompts.value
        if (prompts.isEmpty()) return null
        val dayOfYear = java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_YEAR)
        return prompts[dayOfYear % prompts.size]
    }
    
    fun getJournalStats(): JournalStats {
        val entries = _journalEntries.value
        return JournalStats(
            totalEntries = entries.size,
            entriesThisMonth = entries.count { 
                val c = java.util.Calendar.getInstance()
                c.timeInMillis = it.date
                val current = java.util.Calendar.getInstance()
                c.get(java.util.Calendar.MONTH) == current.get(java.util.Calendar.MONTH)
            },
            currentStreak = 0, // Placeholder
            longestStreak = 0 
        )
    }

    private fun getStartOfDay(timestamp: Long): Long {
        val calendar = java.util.Calendar.getInstance()
        calendar.timeInMillis = timestamp
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
        calendar.set(java.util.Calendar.MINUTE, 0)
        calendar.set(java.util.Calendar.SECOND, 0)
        calendar.set(java.util.Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }
    
    fun addNote(n: Note) {
        viewModelScope.launch {
            storageManager.addNote(n)
            syncToCloud()
        }
    }
    
    fun updateNote(n: Note) {}
    fun deleteNote(id: String) {}
    fun toggleNotePin(id: String) {}
    fun getUserGreeting(): String = "Hello!"
    fun getUpcomingTasks(): List<Task> = emptyList()
    
    fun getGoalById(id: String): Goal? {
        return goals.value.find { it.id == id }
    }
    
    fun getAllItemsForDate(d: Long): List<CalendarItem> = emptyList()

    // ======================== FEATURE METHODS RESTORED ========================

    fun refreshAnalytics() {}
    fun updateSearchQuery(q: String) {
        searchQuery.value = q
        // perform search logic if needed
    }
    fun updateSearchFilters(f: SearchFilters) {
        searchFilters.value = f
    }
    fun clearSearch() {
        searchQuery.value = ""
        searchResults.value = emptyList()
    }
    fun setSelectedDate(t: Long) {
        selectedDate.value = t
    }
    fun addEvent(e: CalendarEvent) {
        viewModelScope.launch {
            storageManager.addEvent(e)
            syncToCloud()
        }
    }
    fun deleteEvent(id: String) {
        viewModelScope.launch {
            storageManager.deleteEvent(id)
            syncToCloud()
        }
    }
    fun toggleMilestone(g: String, m: String) {
        // Toggle milestone logic
        val goal = goals.value.find { it.id == g } ?: return
        val updatedMilestones = goal.milestones.map { 
            if (it.id == m) it.copy(isCompleted = !it.isCompleted, completedAt = if (!it.isCompleted) System.currentTimeMillis() else null) else it 
        }
        val updatedGoal = goal.copy(milestones = updatedMilestones)
        viewModelScope.launch {
            storageManager.updateGoal(updatedGoal)
            loadGoals()
            syncToCloud()
        }
    }
    fun toggleTaskCompletion(t: String) {
        viewModelScope.launch {
            storageManager.toggleTaskCompletion(t)
            syncToCloud()
        }
    }
    fun toggleReminderEnabled(id: String) {
        viewModelScope.launch {
            storageManager.toggleReminderEnabled(id)
            syncToCloud()
        }
    }
    fun deleteTask(t: String) {
        viewModelScope.launch {
            storageManager.deleteTask(t)
            syncToCloud()
        }
    }
    fun addTask(t: Task) {
        viewModelScope.launch {
            storageManager.addTask(t)
            syncToCloud()
        }
    }
    fun updateTask(t: Task) {
        viewModelScope.launch {
            storageManager.updateTask(t)
            syncToCloud()
        }
    }
    fun addReminder(r: Reminder) {
        viewModelScope.launch {
            storageManager.addReminder(r)
            syncToCloud()
        }
    }
    fun updateReminder(r: Reminder) {
        viewModelScope.launch {
            storageManager.updateReminder(r)
            syncToCloud()
        }
    }
    fun deleteReminder(id: String) {
        viewModelScope.launch {
            storageManager.deleteReminder(id)
            syncToCloud()
        }
    }

    // HABITS
    fun addHabit(h: Habit) {
        viewModelScope.launch {
            storageManager.addHabit(h)
            syncToCloud()
        }
    }
    
    fun getHabitStats(id: String): HabitStats {
        val entries = storageManager.getHabitEntries(id).sortedBy { it.date }
        val totalDays = entries.size 
        val completions = entries.count { it.isCompleted }
        
        val heatmap = entries.associate { it.date to if (it.isCompleted) (it.mood?.ordinal?.plus(1) ?: 2) else 0 }
        
        val cal = java.util.Calendar.getInstance()
        val last7 = (0..6).map { i ->
             val d = getStartOfDay(cal.timeInMillis)
             cal.add(java.util.Calendar.DAY_OF_YEAR, -1)
             entries.any { it.date == d && it.isCompleted }
        }.reversed()

        return HabitStats(
            habitId = id,
            currentStreak = completions, 
            totalCompletions = completions,
            completionRate = if (totalDays > 0) completions.toFloat() / 30f else 0f,
            heatmapData = heatmap,
            last7Days = last7
        )
    }

    fun getGlobalHeatmap(): Map<Long, Int> {
        val allEntries = habits.value.flatMap { storageManager.getHabitEntries(it.id) }
        return allEntries.groupBy { it.date }
            .mapValues { (_, entries) -> 
                val count = entries.count { it.isCompleted }
                when {
                    count == 0 -> 0
                    count < 3 -> 1
                    count < 6 -> 2
                    count < 9 -> 3
                    else -> 4
                }
            }
    }

    fun getHabitEntriesForDate(d: Long): List<HabitEntry> = storageManager.getHabitEntriesForDate(d)
    
    fun toggleHabitEntry(id: String, date: Long, value: Float = 1f, mood: HabitMood? = null) {
        viewModelScope.launch {
            val existing = storageManager.getHabitEntries(id).find { it.date == date }
            if (existing != null) {
                if (existing.isCompleted) {
                    storageManager.deleteHabitEntry(existing.id)
                }
            } else {
                val entry = HabitEntry(
                    habitId = id,
                    date = date,
                    isCompleted = true,
                    value = value,
                    mood = mood
                )
                storageManager.addHabitEntry(entry)
            }
            loadHabits() 
            syncToCloud()
        }
    }

    // FINANCE
    fun addTransaction(tr: Transaction) {
        viewModelScope.launch {
            financeRepository.addTransaction(tr)
            loadFinanceData()
            syncToCloud()
        }
    }
    
    fun updateTransaction(tr: Transaction) {
        viewModelScope.launch {
            financeRepository.updateTransaction(tr)
            loadFinanceData()
            syncToCloud()
        }
    }
    
    fun deleteTransaction(id: String) {
        viewModelScope.launch {
            financeRepository.deleteTransaction(id)
            loadFinanceData()
            syncToCloud()
        }
    }

    fun addBudget(b: Budget) {
        viewModelScope.launch {
            financeRepository.addBudget(b)
            loadFinanceData()
            syncToCloud()
        }
    }
    
    fun removeBudget(id: String) {
        viewModelScope.launch {
            financeRepository.removeBudget(id)
            loadFinanceData()
            syncToCloud()
        }
    }

    fun settleDebt(id: String) {
        viewModelScope.launch {
            financeRepository.settleDebt(id)
            loadFinanceData()
            syncToCloud()
        }
    }

    fun exportFinanceCSV(): String = financeRepository.generateTransactionsCSV()

}
