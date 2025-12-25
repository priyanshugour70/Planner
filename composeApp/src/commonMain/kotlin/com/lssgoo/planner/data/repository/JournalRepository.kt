package com.lssgoo.planner.data.repository

import com.lssgoo.planner.features.journal.models.JournalEntry
import com.lssgoo.planner.features.journal.models.JournalPrompt
import com.lssgoo.planner.util.Resource
import kotlinx.coroutines.flow.Flow

interface JournalRepository {
    fun getEntries(): Flow<Resource<List<JournalEntry>>>
    suspend fun saveEntry(entry: JournalEntry): Resource<Boolean>
    suspend fun deleteEntry(id: String): Resource<Boolean>
    
    fun getPrompts(): Flow<Resource<List<JournalPrompt>>>
    suspend fun savePrompts(prompts: List<JournalPrompt>): Resource<Boolean>
}
