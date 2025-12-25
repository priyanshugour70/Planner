package com.lssgoo.planner.data.repository

import com.lssgoo.planner.data.local.AppStorageRepository
import com.lssgoo.planner.features.habits.models.Habit
import com.lssgoo.planner.features.habits.models.HabitEntry
import com.lssgoo.planner.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class HabitRepositoryImpl(
    private val localDataSource: AppStorageRepository
) : HabitRepository {

    override fun getHabits(): Flow<Resource<List<Habit>>> = flow {
        emit(Resource.Loading)
        try {
            val habits = localDataSource.getHabits().filter { !it.isDeleted }
            emit(Resource.Success(habits))
        } catch (e: Exception) {
            emit(Resource.Error(e))
        }
    }

    override suspend fun saveHabit(habit: Habit): Resource<Boolean> {
        return try {
            if (localDataSource.getHabits().any { it.id == habit.id }) {
                localDataSource.updateHabit(habit)
            } else {
                localDataSource.addHabit(habit)
            }
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override suspend fun deleteHabit(id: String): Resource<Boolean> {
        return try {
            val habit = localDataSource.getHabits().find { it.id == id }
            if (habit != null) {
                localDataSource.updateHabit(habit.copy(isDeleted = true))
            }
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override fun getHabitEntries(habitId: String): Flow<Resource<List<HabitEntry>>> = flow {
        emit(Resource.Loading)
        try {
            val entries = localDataSource.getHabitEntries(habitId).filter { !it.isDeleted }
            emit(Resource.Success(entries))
        } catch (e: Exception) {
            emit(Resource.Error(e))
        }
    }

    override suspend fun getHabitEntriesForDate(date: Long): Resource<List<HabitEntry>> {
        return try {
            val entries = localDataSource.getHabitEntriesForDate(date).filter { !it.isDeleted }
            Resource.Success(entries)
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override suspend fun saveHabitEntry(entry: HabitEntry): Resource<Boolean> {
         return try {
             // Logic for add/update in storage is handled by addHabitEntry/updateHabitEntry or strictly updateHabitEntry
             // AppStorageRepository: addHabitEntry removes old with same day+habitId
             // updateHabitEntry checks by ID.
             // We should check by ID.
             if (entry.id.isNotEmpty() && localDataSource.getHabitEntries().any { it.id == entry.id }) {
                 localDataSource.updateHabitEntry(entry)
             } else {
                 localDataSource.addHabitEntry(entry)
             }
             Resource.Success(true)
         } catch (e: Exception) {
             Resource.Error(e)
         }
    }
}
