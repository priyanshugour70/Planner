package com.lssgoo.planner.data.repository

import com.lssgoo.planner.features.tasks.models.Task
import com.lssgoo.planner.features.tasks.models.CalendarEvent
import com.lssgoo.planner.util.Resource
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    fun getTasks(): Flow<Resource<List<Task>>>
    suspend fun getTask(id: String): Resource<Task>
    suspend fun saveTask(task: Task): Resource<Boolean>
    suspend fun deleteTask(id: String): Resource<Boolean>
    suspend fun toggleTaskCompletion(id: String): Resource<Boolean>
    
    fun getEvents(): Flow<Resource<List<CalendarEvent>>>
    suspend fun saveEvent(event: CalendarEvent): Resource<Boolean>
    suspend fun deleteEvent(id: String): Resource<Boolean>
}
