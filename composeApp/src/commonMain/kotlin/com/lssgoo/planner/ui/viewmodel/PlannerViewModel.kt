package com.lssgoo.planner.ui.viewmodel

import androidx.lifecycle.viewModelScope
import com.lssgoo.planner.data.local.AppStorageRepository
import com.lssgoo.planner.data.model.*
import com.lssgoo.planner.data.repository.FinanceRepository
import com.lssgoo.planner.features.habits.models.*
import com.lssgoo.planner.util.KmpTimeUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.*

/**
 * Root ViewModel for global app state - Multiplatform Compatible
 */
class PlannerViewModel(
    private val storageManager: AppStorageRepository,
    private val notificationContext: Any? = null // Platform context for notifications
) : BaseViewModel() {
    
    private val financeRepository = FinanceRepository(storageManager)
    
    private val _settings = MutableStateFlow(storageManager.getSettings())
    val settings: StateFlow<AppSettings> = _settings.asStateFlow()
    
    private val _userProfile = MutableStateFlow(storageManager.getUserProfile() ?: UserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()
    
    private val _isOnboardingComplete = MutableStateFlow(storageManager.isOnboardingComplete())
    val isOnboardingComplete: StateFlow<Boolean> = _isOnboardingComplete.asStateFlow()

    private val _isCheckingSync = MutableStateFlow(false)
    val isCheckingSync: StateFlow<Boolean> = _isCheckingSync.asStateFlow()


    // Feature data states
    val goals = MutableStateFlow<List<Goal>>(emptyList())
    val tasks = MutableStateFlow<List<Task>>(emptyList())
    val notes = MutableStateFlow<List<Note>>(emptyList())
    val reminders = MutableStateFlow<List<Reminder>>(emptyList())
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
    val selectedDate = MutableStateFlow(KmpTimeUtils.currentTimeMillis())
    val events = MutableStateFlow<List<CalendarEvent>>(emptyList())

    // ======================== JOURNAL ========================
    private val _journalEntries = MutableStateFlow<List<JournalEntry>>(emptyList())
    val journalEntries: StateFlow<List<JournalEntry>> = _journalEntries.asStateFlow()
    
    private val _journalPrompts = MutableStateFlow<List<JournalPrompt>>(emptyList())
    val journalPrompts: StateFlow<List<JournalPrompt>> = _journalPrompts.asStateFlow()

    val lastSyncTime = MutableStateFlow(KmpTimeUtils.currentTimeMillis())

    init {
        // Launch data loading in parallel safely
        viewModelScope.launch(Dispatchers.Default) {
            loadFinanceData()
        }
        viewModelScope.launch(Dispatchers.Default) {
            loadHabits()
        }
        viewModelScope.launch(Dispatchers.Default) {
            loadJournalData()
        }
        viewModelScope.launch(Dispatchers.Default) {
            loadGoals()
        }
        viewModelScope.launch(Dispatchers.Default) {
            loadNotes()
        }
        viewModelScope.launch(Dispatchers.Default) {
            loadDashboardStats()
        }
        viewModelScope.launch(Dispatchers.Default) {
            loadReminders()
        }
        viewModelScope.launch(Dispatchers.Default) {
            loadTasks()
        }
        viewModelScope.launch(Dispatchers.Default) {
            loadEvents()
        }
    }
    
    fun saveUserProfile(profile: UserProfile) {
        viewModelScope.launch {
            storageManager.saveUserProfile(profile)
            _userProfile.value = profile
        }
    }
    
    fun setOnboardingComplete(v: Boolean = true) {
        viewModelScope.launch {
            storageManager.setOnboardingComplete()
            _isOnboardingComplete.value = true
        }
    }
    
    fun updateSettings(newSettings: AppSettings) {
        viewModelScope.launch {
            storageManager.saveSettings(newSettings)
            _settings.value = newSettings
        }
    }
    
    fun setPinCode(pin: String?) {
        val current = _settings.value
        updateSettings(current.copy(pinCode = pin))
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
    
    fun importData(json: String): Boolean {
        val success = storageManager.importAllData(json)
        if (success) {
            loadFinanceData()
            loadGoals()
            loadHabits()
            loadJournalData()
            loadNotes()
        }
        return success
    }
    
    fun exportAllData(): String = storageManager.exportAllData()

    private fun loadNotes() {
        viewModelScope.launch(Dispatchers.Default) {
            try {
                notes.value = storageManager.getNotes()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun loadTasks() {
        viewModelScope.launch(Dispatchers.Default) {
            try {
                tasks.value = storageManager.getTasks()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun loadEvents() {
        viewModelScope.launch(Dispatchers.Default) {
            try {
                events.value = storageManager.getEvents()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun loadReminders() {
        viewModelScope.launch(Dispatchers.Default) {
            try {
                reminders.value = storageManager.getReminders()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun loadFinanceData() {
        viewModelScope.launch(Dispatchers.Default) {
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
        viewModelScope.launch(Dispatchers.Default) {
            try {
                val loaded = storageManager.getHabits()
                ensureDefaultHabits(loaded)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    private fun loadJournalData() {
        viewModelScope.launch(Dispatchers.Default) {
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
        viewModelScope.launch(Dispatchers.Default) {
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
        viewModelScope.launch(Dispatchers.Default) {
            try {
                storageManager.addJournalEntry(entry)
                loadJournalData()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    fun deleteJournalEntry(entryId: String) {
        viewModelScope.launch(Dispatchers.Default) {
            try {
                storageManager.deleteJournalEntry(entryId)
                loadJournalData()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getJournalEntryForDate(d: Long): JournalEntry? {
        val dayStart = KmpTimeUtils.getStartOfDay(d)
        val dayEnd = dayStart + 24 * 60 * 60 * 1000 - 1
        return _journalEntries.value.find { it.date in dayStart..dayEnd }
    }
    
    fun getDailyPrompt(): JournalPrompt? {
        val prompts = _journalPrompts.value
        if (prompts.isEmpty()) return null
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val dayOfYear = now.dayOfYear
        return prompts[dayOfYear % prompts.size]
    }
    
    fun getJournalStats(): JournalStats {
        val entries = _journalEntries.value
        val currentMonth = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).monthNumber
        return JournalStats(
            totalEntries = entries.size,
            entriesThisMonth = entries.count { 
                val entryDate = Instant.fromEpochMilliseconds(it.date)
                    .toLocalDateTime(TimeZone.currentSystemDefault())
                entryDate.monthNumber == currentMonth
            },
            currentStreak = 0, // Placeholder
            longestStreak = 0 
        )
    }
    
    fun addNote(n: Note) {
        viewModelScope.launch {
            storageManager.addNote(n)
            loadNotes()
        }
    }
    
    fun updateNote(n: Note) {
        viewModelScope.launch {
            storageManager.updateNote(n)
            loadNotes()
        }
    }
    
    fun deleteNote(id: String) {
        viewModelScope.launch {
            storageManager.deleteNote(id)
            loadNotes()
        }
    }
    
    fun toggleNotePin(id: String) {
        viewModelScope.launch {
            val note = notes.value.find { it.id == id } ?: return@launch
            val updated = note.copy(isPinned = !note.isPinned)
            storageManager.updateNote(updated)
            loadNotes()
        }
    }

    fun getUserGreeting(): String {
        val profile = _userProfile.value
        val hour = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).hour
        val greeting = when {
            hour < 12 -> "Good morning"
            hour < 17 -> "Good afternoon"
            else -> "Good evening"
        }
        return if (profile.firstName.isNotBlank()) "$greeting, ${profile.firstName}!" else "$greeting!"
    }

    fun getUpcomingTasks(): List<Task> {
        val now = KmpTimeUtils.currentTimeMillis()
        val oneDayMs = 24 * 60 * 60 * 1000L
        return tasks.value.filter { task ->
            !task.isCompleted && task.dueDate?.let { it in now..(now + oneDayMs * 7) } == true
        }.sortedBy { it.dueDate }
    }
    
    fun getGoalById(id: String): Goal? {
        return goals.value.find { it.id == id }
    }
    
    fun getAllItemsForDate(d: Long): List<CalendarItem> {
        val items = mutableListOf<CalendarItem>()
        val dayStart = KmpTimeUtils.getStartOfDay(d)
        val dayEnd = dayStart + 24 * 60 * 60 * 1000 - 1
        
        fun isDay(timestamp: Long) = timestamp in dayStart..dayEnd

        // 1. NOTES
        notes.value.forEach { note ->
            if (isDay(note.createdAt)) {
                items.add(CalendarItem(
                    id = "note_new_${note.id}",
                    title = "New Note: ${note.title}",
                    description = note.content.take(60),
                    date = d,
                    type = CalendarItemType.NOTE,
                    priority = note.priority,
                    color = note.color
                ))
            }
            if (note.reminderTime?.let { isDay(it) } == true) {
                items.add(CalendarItem(
                    id = "note_rem_${note.id}",
                    title = "Reminder: ${note.title}",
                    description = note.content.take(60),
                    date = d,
                    type = CalendarItemType.REMINDER,
                    priority = note.priority,
                    color = note.color
                ))
            }
        }

        // 2. GOALS
        goals.value.forEach { goal ->
            if (isDay(goal.createdAt)) {
                items.add(CalendarItem(
                    id = "goal_new_${goal.id}",
                    title = "New Goal: ${goal.title}",
                    description = goal.description,
                    date = d,
                    type = CalendarItemType.GOAL,
                    priority = ItemPriority.P2,
                    color = goal.color
                ))
            }
            goal.milestones.forEach { milestone ->
                if (milestone.completedAt?.let { isDay(it) } == true) {
                    items.add(CalendarItem(
                        id = "milestone_comp_${milestone.id}",
                        title = "Milestone: ${milestone.title}",
                        description = "Achieved in goal: ${goal.title}",
                        date = d,
                        type = CalendarItemType.GOAL_MILESTONE,
                        priority = ItemPriority.P1,
                        color = goal.color,
                        isCompleted = true
                    ))
                }
            }
        }

        // 3. TASKS
        tasks.value.forEach { task ->
            if (task.dueDate?.let { isDay(it) } == true) {
                items.add(CalendarItem(
                    id = "task_due_${task.id}",
                    title = "Task Due: ${task.title}",
                    description = task.description,
                    date = d,
                    type = CalendarItemType.TASK,
                    priority = ItemPriority.P4,
                    color = 0xFF2196F3,
                    isCompleted = task.isCompleted
                ))
            }
        }

        // 4. JOURNAL
        journalEntries.value.forEach { entry ->
            if (isDay(entry.date)) {
                items.add(CalendarItem(
                    id = "journal_${entry.id}",
                    title = "Journal: ${entry.mood.emoji} ${entry.title.ifBlank { "Daily Reflection" }}",
                    description = entry.content.take(60),
                    date = d,
                    type = CalendarItemType.JOURNAL,
                    priority = ItemPriority.P5,
                    color = entry.mood.color
                ))
            }
        }

        // 5. FINANCE
        transactions.value.forEach { txn ->
            if (isDay(txn.date)) {
                items.add(CalendarItem(
                    id = "txn_${txn.id}",
                    title = "Finance: ${txn.category.icon} ${txn.type} ₹${txn.amount}",
                    description = txn.note,
                    date = d,
                    type = CalendarItemType.FINANCE,
                    priority = if (txn.amount > 5000) ItemPriority.P1 else ItemPriority.P3,
                    color = if (txn.type.name == "EXPENSE") 0xFFF44336 else 0xFF4CAF50
                ))
            }
        }

        // 6. HABITS
        habits.value.forEach { habit ->
            val entries = storageManager.getHabitEntries(habit.id)
            entries.forEach { entry ->
                if (isDay(entry.date) && entry.isCompleted) {
                    items.add(CalendarItem(
                        id = "habit_${entry.id}",
                        title = "Habit: ${habit.icon} ${habit.title}",
                        description = "Completed target: ${habit.targetValue} ${habit.unit ?: ""}",
                        date = d,
                        type = CalendarItemType.HABIT,
                        priority = ItemPriority.P6,
                        color = habit.iconColor,
                        isCompleted = true
                    ))
                }
            }
        }

        // 7. EVENTS & REMINDERS
        events.value.forEach { event ->
            if (isDay(event.date)) {
                items.add(CalendarItem(
                    id = "event_${event.id}",
                    title = "Event: ${event.title}",
                    description = event.description,
                    date = d,
                    type = CalendarItemType.EVENT,
                    priority = ItemPriority.P3,
                    color = 0xFFFFC107
                ))
            }
        }
        
        reminders.value.forEach { reminder ->
            if (isDay(reminder.reminderTime)) {
                items.add(CalendarItem(
                    id = "rem_${reminder.id}",
                    title = "Reminder: ${reminder.title}",
                    description = reminder.description,
                    date = d,
                    type = CalendarItemType.REMINDER,
                    priority = reminder.priority,
                    color = reminder.color
                ))
            }
        }

        return items.sortedBy { it.priority.level }
    }

    // ======================== FEATURE METHODS ========================

    fun refreshAnalytics() {}

    fun updateSearchQuery(q: String) {
        searchQuery.value = q
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
            loadEvents()
        }
    }

    fun deleteEvent(id: String) {
        viewModelScope.launch {
            storageManager.deleteEvent(id)
            loadEvents()
        }
    }

    fun getMonthActivityCounts(year: Int, month: Int): Map<Int, Int> {
        val counts = mutableMapOf<Int, Int>()
        val tz = TimeZone.currentSystemDefault()
        
        fun addCount(t: Long) {
            val d = Instant.fromEpochMilliseconds(t).toLocalDateTime(tz).date
            if (d.year == year && d.monthNumber == month) {
                counts[d.dayOfMonth] = (counts[d.dayOfMonth] ?: 0) + 1
            }
        }
        
        tasks.value.forEach { it.dueDate?.let { addCount(it) } }
        events.value.forEach { addCount(it.date) }
        reminders.value.forEach { addCount(it.reminderTime) }
        goals.value.forEach { it.milestones.forEach { m -> m.completedAt?.let { addCount(it) } } }
        _journalEntries.value.forEach { addCount(it.date) }
        transactions.value.forEach { addCount(it.date) }
        
        return counts
    }

    fun getGlobalHeatmap(): Map<Long, Int> {
        val heatmap = mutableMapOf<Long, Int>()
        
        // Count completions from habits
        habits.value.forEach { habit ->
            val entries = storageManager.getHabitEntries(habit.id)
            entries.forEach { entry ->
                if (entry.isCompleted) {
                    val day = KmpTimeUtils.getStartOfDay(entry.date)
                    heatmap[day] = (heatmap[day] ?: 0) + 1
                }
            }
        }
        
        // Add tasks
        tasks.value.forEach { task ->
            if (task.isCompleted && task.completedAt != null) {
                val day = KmpTimeUtils.getStartOfDay(task.completedAt)
                heatmap[day] = (heatmap[day] ?: 0) + 1
            }
        }
        
        // Add milestones
        goals.value.forEach { goal ->
            goal.milestones.forEach { m ->
                if (m.isCompleted && m.completedAt != null) {
                    val day = KmpTimeUtils.getStartOfDay(m.completedAt)
                    heatmap[day] = (heatmap[day] ?: 0) + 1
                }
            }
        }
        
        return heatmap
    }

    fun toggleMilestone(g: String, m: String) {
        val goal = goals.value.find { it.id == g } ?: return
        val updatedMilestones = goal.milestones.map { 
            if (it.id == m) it.copy(
                isCompleted = !it.isCompleted, 
                completedAt = if (!it.isCompleted) KmpTimeUtils.currentTimeMillis() else null
            ) else it 
        }
        val updatedGoal = goal.copy(milestones = updatedMilestones)
        viewModelScope.launch {
            storageManager.updateGoal(updatedGoal)
            loadGoals()
        }
    }

    fun addGoal(g: Goal) {
        viewModelScope.launch {
            storageManager.updateGoal(g)
            loadGoals()
        }
    }

    fun updateGoal(g: Goal) {
        viewModelScope.launch {
            storageManager.updateGoal(g)
            loadGoals()
        }
    }

    fun deleteGoal(id: String) {
        viewModelScope.launch {
            storageManager.deleteGoal(id)
            loadGoals()
        }
    }

    fun updateMilestone(goalId: String, m: Milestone) {
        val goal = goals.value.find { it.id == goalId } ?: return
        val updatedMilestones = goal.milestones.map { if (it.id == m.id) m else it }
        updateGoal(goal.copy(milestones = updatedMilestones))
    }

    fun deleteMilestone(goalId: String, milestoneId: String) {
        val goal = goals.value.find { it.id == goalId } ?: return
        val updatedMilestones = goal.milestones.filter { it.id != milestoneId }
        updateGoal(goal.copy(milestones = updatedMilestones))
    }

    fun toggleTaskCompletion(t: String) {
        viewModelScope.launch {
            storageManager.toggleTaskCompletion(t)
            loadTasks()
        }
    }

    fun toggleReminderEnabled(id: String) {
        viewModelScope.launch {
            storageManager.toggleReminderEnabled(id)
            loadReminders()
        }
    }

    fun deleteTask(t: String) {
        viewModelScope.launch {
            storageManager.deleteTask(t)
            loadTasks()
        }
    }

    fun addTask(t: Task) {
        viewModelScope.launch {
            storageManager.addTask(t)
            loadTasks()
        }
    }

    fun updateTask(t: Task) {
        viewModelScope.launch {
            storageManager.updateTask(t)
            loadTasks()
        }
    }

    fun addReminder(r: Reminder) {
        viewModelScope.launch {
            storageManager.addReminder(r)
            loadReminders()
            // Notification scheduling would be handled by platform-specific code
        }
    }

    fun updateReminder(r: Reminder) {
        viewModelScope.launch {
            storageManager.updateReminder(r)
            loadReminders()
        }
    }

    fun deleteReminder(id: String) {
        viewModelScope.launch {
            storageManager.deleteReminder(id)
            loadReminders()
        }
    }

    // HABITS
    fun addHabit(h: Habit) {
        viewModelScope.launch {
            storageManager.addHabit(h)
            loadHabits()
        }
    }
    
    fun getHabitStats(id: String): HabitStats {
        val entries = storageManager.getHabitEntries(id).sortedBy { it.date }
        val totalDays = entries.size 
        val completions = entries.count { it.isCompleted }
        
        val heatmap = entries.associate { it.date to if (it.isCompleted) (it.mood?.ordinal?.plus(1) ?: 2) else 0 }
        
        val now = KmpTimeUtils.currentTimeMillis()
        val oneDayMs = 24 * 60 * 60 * 1000L
        val last7 = (0..6).map { i ->
            val d = KmpTimeUtils.getStartOfDay(now - (i * oneDayMs))
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
        }
    }

    fun deleteHabit(id: String) {
        viewModelScope.launch {
            storageManager.deleteHabit(id)
            loadHabits()
        }
    }

    // CLOUD SYNC
    fun syncToCloud() {
        viewModelScope.launch {
            _isSyncing.value = true
            // Mock sync for now
            kotlinx.coroutines.delay(2000)
            lastSyncTime.value = KmpTimeUtils.currentTimeMillis()
            _isSyncing.value = false
        }
    }

    fun syncFromCloud() {
        viewModelScope.launch {
            _isSyncing.value = true
            // Mock sync for now
            kotlinx.coroutines.delay(2000)
            loadGoals()
            loadTasks()
            loadHabits()
            _isSyncing.value = false
        }
    }

    // FINANCE
    fun addTransaction(tr: Transaction) {
        viewModelScope.launch {
            financeRepository.addTransaction(tr)
            loadFinanceData()
        }
    }
    
    fun updateTransaction(tr: Transaction) {
        viewModelScope.launch {
            financeRepository.updateTransaction(tr)
            loadFinanceData()
        }
    }
    
    fun deleteTransaction(id: String) {
        viewModelScope.launch {
            financeRepository.deleteTransaction(id)
            loadFinanceData()
        }
    }

    fun addBudget(b: Budget) {
        viewModelScope.launch {
            financeRepository.addBudget(b)
            loadFinanceData()
        }
    }
    
    fun removeBudget(id: String) {
        viewModelScope.launch {
            financeRepository.removeBudget(id)
            loadFinanceData()
        }
    }

    fun settleDebt(id: String) {
        viewModelScope.launch {
            financeRepository.settleDebt(id)
            loadFinanceData()
        }
    }

    fun exportFinanceCSV(): String = financeRepository.generateTransactionsCSV()

    fun loadDashboardStats() {
        viewModelScope.launch(Dispatchers.Default) {
            // Simplified dashboard stats calculation
            val goalsVal = goals.value
            val tasksVal = tasks.value
            val habitsVal = habits.value
            
            val today = KmpTimeUtils.getStartOfDay(KmpTimeUtils.currentTimeMillis())
            val todayEnd = today + 24 * 60 * 60 * 1000 - 1
            
            val totalMilestones = goalsVal.sumOf { it.milestones.size }
            val completedMilestones = goalsVal.sumOf { goal -> 
                goal.milestones.count { it.isCompleted } 
            }
            
            val todayTasks = tasksVal.filter { task ->
                task.dueDate?.let { it in today..todayEnd } ?: false
            }
            
            val todayHabits = habitsVal.filter { it.isActive }
            val todayEntries = storageManager.getHabitEntriesForDate(today).associateBy { it.habitId }
            val habitsCompleted = todayHabits.count { habit ->
                todayEntries[habit.id]?.isCompleted == true
            }

            val overallProgress = if (totalMilestones > 0) {
                completedMilestones.toFloat() / totalMilestones.toFloat()
            } else 0f
            
            dashboardStats.value = DashboardStats(
                totalGoals = goalsVal.size,
                completedMilestones = completedMilestones,
                totalMilestones = totalMilestones,
                tasksCompletedToday = todayTasks.count { it.isCompleted },
                totalTasksToday = todayTasks.size,
                currentStreak = 0,
                longestStreak = 0,
                overallProgress = overallProgress,
                totalHabitsToday = todayHabits.size,
                habitsCompletedToday = habitsCompleted
            )
        }
    }
}
