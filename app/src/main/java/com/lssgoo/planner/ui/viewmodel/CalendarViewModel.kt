package com.lssgoo.planner.ui.viewmodel

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.lssgoo.planner.data.local.LocalStorageManager
import com.lssgoo.planner.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

/**
 * ViewModel for Calendar feature
 */
class CalendarViewModel(application: Application) : BaseViewModel(application) {
    
    private val storageManager = LocalStorageManager(application)
    
    private val _selectedDate = MutableStateFlow(System.currentTimeMillis())
    val selectedDate: StateFlow<Long> = _selectedDate.asStateFlow()
    
    private val _events = MutableStateFlow<List<CalendarEvent>>(emptyList())
    val events: StateFlow<List<CalendarEvent>> = _events.asStateFlow()
    
    init {
        loadEvents()
    }
    
    fun loadEvents() {
        viewModelScope.launch(Dispatchers.IO) {
            _events.value = storageManager.getEvents()
        }
    }
    
    fun setSelectedDate(timestamp: Long) {
        _selectedDate.value = timestamp
    }
    
    fun addEvent(event: CalendarEvent) {
        viewModelScope.launch(Dispatchers.IO) {
            val list = storageManager.getEvents().toMutableList()
            list.add(0, event)
            storageManager.saveEvents(list)
            loadEvents()
        }
    }
    
    fun getAllItemsForDate(timestamp: Long): List<CalendarItem> {
        val date = Calendar.getInstance().apply { timeInMillis = timestamp }
        val d = date.get(Calendar.DAY_OF_YEAR)
        val y = date.get(Calendar.YEAR)
        
        val items = mutableListOf<CalendarItem>()
        
        // Tasks
        storageManager.getTasks().forEach { task ->
            if (task.dueDate != null) {
                date.timeInMillis = task.dueDate
                if (date.get(Calendar.DAY_OF_YEAR) == d && date.get(Calendar.YEAR) == y) {
                    items.add(CalendarItem(
                        id = task.id,
                        title = task.title,
                        description = task.description,
                        date = task.dueDate,
                        type = CalendarItemType.TASK,
                        priority = ItemPriority.P5, // Map TaskPriority to ItemPriority
                        color = task.priority.color,
                        isCompleted = task.isCompleted
                    ))
                }
            }
        }
        
        // Reminders, Events...
        
        return items
    }
}
