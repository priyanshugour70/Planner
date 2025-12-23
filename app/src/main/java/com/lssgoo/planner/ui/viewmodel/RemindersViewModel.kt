package com.lssgoo.planner.ui.viewmodel

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.lssgoo.planner.data.local.LocalStorageManager
import com.lssgoo.planner.data.model.Reminder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for Reminders feature
 */
class RemindersViewModel(application: Application) : BaseViewModel(application) {
    
    private val storageManager = LocalStorageManager(application)
    
    private val _reminders = MutableStateFlow<List<Reminder>>(emptyList())
    val reminders: StateFlow<List<Reminder>> = _reminders.asStateFlow()
    
    init {
        loadReminders()
    }
    
    fun loadReminders() {
        viewModelScope.launch(Dispatchers.IO) {
            _reminders.value = storageManager.getReminders()
        }
    }
    
    fun addReminder(reminder: Reminder) {
        viewModelScope.launch(Dispatchers.IO) {
            val list = storageManager.getReminders().toMutableList()
            list.add(0, reminder)
            storageManager.saveReminders(list)
            loadReminders()
        }
    }
    
    fun updateReminder(reminder: Reminder) {
        viewModelScope.launch(Dispatchers.IO) {
            val list = storageManager.getReminders().toMutableList()
            val index = list.indexOfFirst { it.id == reminder.id }
            if (index != -1) {
                list[index] = reminder
                storageManager.saveReminders(list)
                loadReminders()
            }
        }
    }
    
    fun deleteReminder(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val list = storageManager.getReminders().filter { it.id != id }
            storageManager.saveReminders(list)
            loadReminders()
        }
    }
    
    fun toggleReminderEnabled(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val list = storageManager.getReminders().toMutableList()
            val index = list.indexOfFirst { it.id == id }
            if (index != -1) {
                list[index] = list[index].copy(isEnabled = !list[index].isEnabled)
                storageManager.saveReminders(list)
                loadReminders()
            }
        }
    }
}
