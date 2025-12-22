package com.lssgoo.goal2026.ui.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.lssgoo.goal2026.data.local.LocalStorageManager
import com.lssgoo.goal2026.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class Goal2026ViewModel(application: Application) : AndroidViewModel(application) {
    
    private val storageManager = LocalStorageManager(application)
    
    // Goals State
    private val _goals = MutableStateFlow<List<Goal>>(emptyList())
    val goals: StateFlow<List<Goal>> = _goals.asStateFlow()
    
    // Notes State
    private val _notes = MutableStateFlow<List<Note>>(emptyList())
    val notes: StateFlow<List<Note>> = _notes.asStateFlow()
    
    // Tasks State
    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()
    
    // Events State
    private val _events = MutableStateFlow<List<CalendarEvent>>(emptyList())
    val events: StateFlow<List<CalendarEvent>> = _events.asStateFlow()
    
    // Reminders State
    private val _reminders = MutableStateFlow<List<Reminder>>(emptyList())
    val reminders: StateFlow<List<Reminder>> = _reminders.asStateFlow()
    
    // User Profile State
    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile.asStateFlow()
    
    // Onboarding State
    private val _isOnboardingComplete = MutableStateFlow(false)
    val isOnboardingComplete: StateFlow<Boolean> = _isOnboardingComplete.asStateFlow()
    
    // Dashboard Stats
    private val _dashboardStats = MutableStateFlow(DashboardStats())
    val dashboardStats: StateFlow<DashboardStats> = _dashboardStats.asStateFlow()
    
    // Settings
    private val _settings = MutableStateFlow(AppSettings())
    val settings: StateFlow<AppSettings> = _settings.asStateFlow()
    
    // Selected date for calendar
    private val _selectedDate = MutableStateFlow(System.currentTimeMillis())
    val selectedDate: StateFlow<Long> = _selectedDate.asStateFlow()
    
    // Loading states
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    // Snackbar messages
    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()
    
    init {
        loadAllData()
    }
    
    private fun loadAllData() {
        viewModelScope.launch {
            _isLoading.value = true
            withContext(Dispatchers.IO) {
                // Load onboarding status first
                _isOnboardingComplete.value = storageManager.isOnboardingComplete()
                _userProfile.value = storageManager.getUserProfile()
                
                // Check if first launch and initialize default goals
                if (storageManager.isFirstLaunch()) {
                    storageManager.saveGoals(DefaultGoals.goals)
                    storageManager.setFirstLaunchComplete()
                }
                
                _goals.value = storageManager.getGoals()
                _notes.value = storageManager.getNotes()
                _tasks.value = storageManager.getTasks()
                _events.value = storageManager.getEvents()
                _reminders.value = storageManager.getReminders()
                _settings.value = storageManager.getSettings()
                _dashboardStats.value = storageManager.getDashboardStats()
            }
            _isLoading.value = false
        }
    }
    
    fun refreshData() {
        loadAllData()
    }
    
    // ======================== GOALS ========================
    
    fun updateGoal(goal: Goal) {
        viewModelScope.launch(Dispatchers.IO) {
            storageManager.updateGoal(goal)
            _goals.value = storageManager.getGoals()
            _dashboardStats.value = storageManager.getDashboardStats()
        }
    }
    
    fun toggleMilestone(goalId: String, milestoneId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val goals = storageManager.getGoals().toMutableList()
            val goalIndex = goals.indexOfFirst { it.id == goalId }
            
            if (goalIndex != -1) {
                val goal = goals[goalIndex]
                val updatedMilestones = goal.milestones.map { milestone ->
                    if (milestone.id == milestoneId) {
                        milestone.copy(
                            isCompleted = !milestone.isCompleted,
                            completedAt = if (!milestone.isCompleted) System.currentTimeMillis() else null
                        )
                    } else milestone
                }
                
                val completedCount = updatedMilestones.count { it.isCompleted }
                val newProgress = if (updatedMilestones.isNotEmpty()) {
                    completedCount.toFloat() / updatedMilestones.size
                } else 0f
                
                goals[goalIndex] = goal.copy(
                    milestones = updatedMilestones,
                    progress = newProgress,
                    updatedAt = System.currentTimeMillis()
                )
                
                storageManager.saveGoals(goals)
                _goals.value = goals
                _dashboardStats.value = storageManager.getDashboardStats()
            }
        }
    }
    
    fun getGoalById(goalId: String): Goal? {
        return _goals.value.find { it.id == goalId }
    }
    
    // ======================== NOTES ========================
    
    fun addNote(note: Note) {
        viewModelScope.launch(Dispatchers.IO) {
            storageManager.addNote(note)
            _notes.value = storageManager.getNotes()
        }
        showSnackbar("Note added successfully!")
    }
    
    fun updateNote(note: Note) {
        viewModelScope.launch(Dispatchers.IO) {
            storageManager.updateNote(note)
            _notes.value = storageManager.getNotes()
        }
        showSnackbar("Note updated!")
    }
    
    fun deleteNote(noteId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            storageManager.deleteNote(noteId)
            _notes.value = storageManager.getNotes()
        }
        showSnackbar("Note deleted")
    }
    
    fun toggleNotePin(noteId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val notes = storageManager.getNotes().toMutableList()
            val index = notes.indexOfFirst { it.id == noteId }
            if (index != -1) {
                notes[index] = notes[index].copy(isPinned = !notes[index].isPinned)
                storageManager.saveNotes(notes)
                _notes.value = notes.sortedByDescending { it.isPinned }
            }
        }
    }
    
    fun getNoteById(noteId: String): Note? {
        return _notes.value.find { it.id == noteId }
    }
    
    // ======================== TASKS ========================
    
    fun addTask(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            storageManager.addTask(task)
            _tasks.value = storageManager.getTasks()
            _dashboardStats.value = storageManager.getDashboardStats()
        }
        showSnackbar("Task added!")
    }
    
    fun updateTask(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            storageManager.updateTask(task)
            _tasks.value = storageManager.getTasks()
            _dashboardStats.value = storageManager.getDashboardStats()
        }
    }
    
    fun deleteTask(taskId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            storageManager.deleteTask(taskId)
            _tasks.value = storageManager.getTasks()
            _dashboardStats.value = storageManager.getDashboardStats()
        }
        showSnackbar("Task deleted")
    }
    
    fun toggleTaskCompletion(taskId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            storageManager.toggleTaskCompletion(taskId)
            _tasks.value = storageManager.getTasks()
            _dashboardStats.value = storageManager.getDashboardStats()
        }
    }
    
    fun getTasksForDate(date: Long): List<Task> {
        val dayStart = getStartOfDay(date)
        val dayEnd = dayStart + 24 * 60 * 60 * 1000 - 1
        return _tasks.value.filter { task ->
            task.dueDate?.let { it in dayStart..dayEnd } ?: false
        }
    }
    
    fun getTodayTasks(): List<Task> {
        return getTasksForDate(System.currentTimeMillis())
    }
    
    fun getUpcomingTasks(): List<Task> {
        val now = System.currentTimeMillis()
        return _tasks.value
            .filter { !it.isCompleted && (it.dueDate == null || it.dueDate >= now) }
            .sortedBy { it.dueDate ?: Long.MAX_VALUE }
            .take(5)
    }
    
    // ======================== EVENTS ========================
    
    fun addEvent(event: CalendarEvent) {
        viewModelScope.launch(Dispatchers.IO) {
            storageManager.addEvent(event)
            _events.value = storageManager.getEvents()
        }
        showSnackbar("Event added to calendar!")
    }
    
    fun updateEvent(event: CalendarEvent) {
        viewModelScope.launch(Dispatchers.IO) {
            storageManager.updateEvent(event)
            _events.value = storageManager.getEvents()
        }
    }
    
    fun deleteEvent(eventId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            storageManager.deleteEvent(eventId)
            _events.value = storageManager.getEvents()
        }
        showSnackbar("Event deleted")
    }
    
    fun getEventsForDate(date: Long): List<CalendarEvent> {
        val dayStart = getStartOfDay(date)
        val dayEnd = dayStart + 24 * 60 * 60 * 1000 - 1
        return _events.value.filter { it.date in dayStart..dayEnd }
    }
    
    fun setSelectedDate(date: Long) {
        _selectedDate.value = date
    }
    
    // ======================== SETTINGS ========================
    
    fun updateSettings(newSettings: AppSettings) {
        viewModelScope.launch(Dispatchers.IO) {
            storageManager.saveSettings(newSettings)
            _settings.value = newSettings
        }
    }
    
    // ======================== BACKUP & RESTORE ========================
    
    fun exportData(): String {
        return storageManager.exportAllData()
    }
    
    fun exportDataToFile(context: Context): Uri? {
        return try {
            val jsonData = storageManager.exportAllData()
            val dateFormat = SimpleDateFormat("yyyy-MM-dd_HHmmss", Locale.getDefault())
            val fileName = "Goal2026_Backup_${dateFormat.format(Date())}.json"
            
            val file = File(context.cacheDir, fileName)
            file.writeText(jsonData)
            
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    fun importData(jsonData: String): Boolean {
        val success = storageManager.importAllData(jsonData)
        if (success) {
            loadAllData()
            showSnackbar("Data restored successfully!")
        } else {
            showSnackbar("Failed to restore data. Invalid backup file.")
        }
        return success
    }
    
    fun clearAllData() {
        viewModelScope.launch(Dispatchers.IO) {
            storageManager.clearAllData()
            // Reinitialize with default goals
            storageManager.saveGoals(DefaultGoals.goals)
            loadAllData()
        }
        showSnackbar("All data cleared. Default goals restored.")
    }
    
    // ======================== UTILITIES ========================
    
    private fun showSnackbar(message: String) {
        _snackbarMessage.value = message
    }
    
    fun clearSnackbar() {
        _snackbarMessage.value = null
    }
    
    private fun getStartOfDay(timestamp: Long): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }
    
    // ======================== USER PROFILE ========================
    
    fun saveUserProfile(profile: UserProfile) {
        viewModelScope.launch(Dispatchers.IO) {
            storageManager.saveUserProfile(profile)
            _userProfile.value = profile
        }
        showSnackbar("Profile saved!")
    }
    
    fun updateUserProfile(profile: UserProfile) {
        viewModelScope.launch(Dispatchers.IO) {
            val updatedProfile = profile.copy(updatedAt = System.currentTimeMillis())
            storageManager.saveUserProfile(updatedProfile)
            _userProfile.value = updatedProfile
        }
    }
    
    fun setOnboardingComplete() {
        viewModelScope.launch(Dispatchers.IO) {
            storageManager.setOnboardingComplete()
            _isOnboardingComplete.value = true
        }
    }
    
    fun getUserGreeting(): String {
        return _userProfile.value?.greeting ?: run {
            val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
            when {
                hour < 12 -> "Good Morning!"
                hour < 17 -> "Good Afternoon!"
                else -> "Good Evening!"
            }
        }
    }
    
    // ======================== REMINDERS ========================
    
    fun addReminder(reminder: Reminder) {
        viewModelScope.launch(Dispatchers.IO) {
            storageManager.addReminder(reminder)
            _reminders.value = storageManager.getReminders()
        }
        showSnackbar("Reminder set!")
    }
    
    fun updateReminder(reminder: Reminder) {
        viewModelScope.launch(Dispatchers.IO) {
            storageManager.updateReminder(reminder)
            _reminders.value = storageManager.getReminders()
        }
    }
    
    fun deleteReminder(reminderId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            storageManager.deleteReminder(reminderId)
            _reminders.value = storageManager.getReminders()
        }
        showSnackbar("Reminder deleted")
    }
    
    fun toggleReminderEnabled(reminderId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            storageManager.toggleReminderEnabled(reminderId)
            _reminders.value = storageManager.getReminders()
        }
    }
    
    fun getRemindersForDate(date: Long): List<Reminder> {
        val dayStart = getStartOfDay(date)
        val dayEnd = dayStart + 24 * 60 * 60 * 1000 - 1
        return _reminders.value.filter { it.reminderTime in dayStart..dayEnd }
    }
    
    fun getUpcomingReminders(): List<Reminder> {
        val now = System.currentTimeMillis()
        return _reminders.value
            .filter { it.isEnabled && !it.isCompleted && it.reminderTime >= now }
            .sortedBy { it.reminderTime }
            .take(5)
    }
    
    // ======================== CALENDAR INTEGRATION ========================
    
    /**
     * Get all items for a specific date (tasks, events, reminders, notes with reminders)
     * This provides unified calendar view
     */
    fun getAllItemsForDate(date: Long): List<CalendarItem> {
        val dayStart = getStartOfDay(date)
        val dayEnd = dayStart + 24 * 60 * 60 * 1000 - 1
        
        val items = mutableListOf<CalendarItem>()
        
        // Add tasks
        _tasks.value.filter { task ->
            task.dueDate?.let { it in dayStart..dayEnd } ?: false
        }.forEach { task ->
            items.add(
                CalendarItem(
                    id = task.id,
                    title = task.title,
                    description = task.description,
                    date = task.dueDate ?: date,
                    type = CalendarItemType.TASK,
                    priority = task.itemPriority,
                    color = task.priority.color,
                    isCompleted = task.isCompleted,
                    linkedGoalId = task.linkedGoalId
                )
            )
        }
        
        // Add events
        _events.value.filter { event ->
            event.date in dayStart..dayEnd
        }.forEach { event ->
            items.add(
                CalendarItem(
                    id = event.id,
                    title = event.title,
                    description = event.description,
                    date = event.date,
                    type = CalendarItemType.EVENT,
                    priority = event.priority,
                    color = event.color,
                    linkedGoalId = event.linkedGoalId
                )
            )
        }
        
        // Add reminders
        _reminders.value.filter { reminder ->
            reminder.reminderTime in dayStart..dayEnd
        }.forEach { reminder ->
            items.add(
                CalendarItem(
                    id = reminder.id,
                    title = reminder.title,
                    description = reminder.description,
                    date = reminder.reminderTime,
                    type = CalendarItemType.REMINDER,
                    priority = reminder.priority,
                    color = reminder.color,
                    isCompleted = reminder.isCompleted,
                    linkedGoalId = reminder.linkedGoalId
                )
            )
        }
        
        // Add notes with reminders
        _notes.value.filter { note ->
            note.hasReminder && note.reminderTime?.let { it in dayStart..dayEnd } ?: false
        }.forEach { note ->
            items.add(
                CalendarItem(
                    id = note.id,
                    title = note.title,
                    description = note.content.take(100),
                    date = note.reminderTime ?: date,
                    type = CalendarItemType.NOTE,
                    priority = note.priority,
                    color = note.color,
                    linkedGoalId = note.linkedGoalId
                )
            )
        }
        
        return items.sortedBy { it.date }
    }
}
