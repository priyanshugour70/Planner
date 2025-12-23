package com.lssgoo.planner.ui.viewmodel

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.lssgoo.planner.data.local.LocalStorageManager
import com.lssgoo.planner.data.model.JournalEntry
import com.lssgoo.planner.data.repository.JournalRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for Journal feature
 */
class JournalViewModel(application: Application) : BaseViewModel(application) {
    
    private val storageManager = LocalStorageManager(application)
    private val journalRepo = JournalRepository(storageManager)
    
    private val _entries = MutableStateFlow<List<JournalEntry>>(emptyList())
    val entries: StateFlow<List<JournalEntry>> = _entries.asStateFlow()
    
    init {
        loadEntries()
    }
    
    fun loadEntries() {
        viewModelScope.launch(Dispatchers.IO) {
            _entries.value = journalRepo.getEntries()
        }
    }
    
    fun addEntry(entry: JournalEntry) {
        viewModelScope.launch(Dispatchers.IO) {
            journalRepo.saveEntry(entry)
            loadEntries()
        }
    }
    
    fun deleteEntry(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            journalRepo.deleteEntry(id)
            loadEntries()
        }
    }
    
    fun getEntryForDate(date: Long): JournalEntry? {
        val cal = java.util.Calendar.getInstance()
        cal.timeInMillis = date
        val d = cal.get(java.util.Calendar.DAY_OF_YEAR)
        val y = cal.get(java.util.Calendar.YEAR)
        
        return _entries.value.find { 
            cal.timeInMillis = it.date
            cal.get(java.util.Calendar.DAY_OF_YEAR) == d && cal.get(java.util.Calendar.YEAR) == y
        }
    }
}
