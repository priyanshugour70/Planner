package com.lssgoo.planner.data.repository

import com.lssgoo.planner.data.local.AppStorageRepository
import com.lssgoo.planner.features.journal.models.JournalEntry
import com.lssgoo.planner.features.journal.models.JournalPrompt
import com.lssgoo.planner.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class JournalRepositoryImpl(
    private val localDataSource: AppStorageRepository
) : JournalRepository {

    override fun getEntries(): Flow<Resource<List<JournalEntry>>> = flow {
        emit(Resource.Loading)
        try {
            val entries = localDataSource.getJournalEntries().filter { !it.isDeleted }
            emit(Resource.Success(entries))
        } catch (e: Exception) {
            emit(Resource.Error(e))
        }
    }

    override suspend fun saveEntry(entry: JournalEntry): Resource<Boolean> {
        return try {
            if (localDataSource.getJournalEntries().any { it.id == entry.id }) {
                localDataSource.updateJournalEntry(entry)
            } else {
                localDataSource.addJournalEntry(entry)
            }
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override suspend fun deleteEntry(id: String): Resource<Boolean> {
        return try {
            val entry = localDataSource.getJournalEntries().find { it.id == id }
            if (entry != null) {
                // Implementing soft delete
                localDataSource.updateJournalEntry(entry.copy(isDeleted = true))
            }
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override fun getPrompts(): Flow<Resource<List<JournalPrompt>>> = flow {
         emit(Resource.Loading)
         try {
             val prompts = localDataSource.getJournalPrompts()
             emit(Resource.Success(prompts))
         } catch (e: Exception) {
             emit(Resource.Error(e))
         }
    }

    override suspend fun savePrompts(prompts: List<JournalPrompt>): Resource<Boolean> {
        return try {
            localDataSource.saveJournalPrompts(prompts)
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }
}
