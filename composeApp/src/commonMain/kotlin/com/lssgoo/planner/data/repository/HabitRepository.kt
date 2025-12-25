package com.lssgoo.planner.data.repository

import com.lssgoo.planner.features.habits.models.Habit
import com.lssgoo.planner.features.habits.models.HabitEntry
import com.lssgoo.planner.util.Resource
import kotlinx.coroutines.flow.Flow

interface HabitRepository {
    fun getHabits(): Flow<Resource<List<Habit>>>
    suspend fun saveHabit(habit: Habit): Resource<Boolean>
    suspend fun deleteHabit(id: String): Resource<Boolean>
    
    fun getHabitEntries(habitId: String): Flow<Resource<List<HabitEntry>>>
    suspend fun getHabitEntriesForDate(date: Long): Resource<List<HabitEntry>>
    suspend fun saveHabitEntry(entry: HabitEntry): Resource<Boolean>
}
