package com.lssgoo.planner.data.repository

import com.lssgoo.planner.data.local.LocalStorageManager
import com.lssgoo.planner.data.model.JournalEntry

/**
 * Repository for managing Journal entries
 */
class JournalRepository(private val storage: LocalStorageManager) {
    
    fun getEntries(): List<JournalEntry> {
        return storage.getJournalEntries()
    }
    
    fun saveEntry(entry: JournalEntry) {
        val entries = storage.getJournalEntries().toMutableList()
        val index = entries.indexOfFirst { it.id == entry.id }
        if (index != -1) {
            entries[index] = entry.copy(updatedAt = System.currentTimeMillis())
        } else {
            entries.add(0, entry)
        }
        storage.saveJournalEntries(entries)
    }
    
    fun deleteEntry(id: String) {
        val entries = storage.getJournalEntries().filter { it.id != id }
        storage.saveJournalEntries(entries)
    }
}
