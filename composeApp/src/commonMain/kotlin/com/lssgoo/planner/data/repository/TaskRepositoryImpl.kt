package com.lssgoo.planner.data.repository

import com.lssgoo.planner.data.local.AppStorageRepository
import com.lssgoo.planner.features.tasks.models.Task
import com.lssgoo.planner.features.tasks.models.CalendarEvent
import com.lssgoo.planner.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TaskRepositoryImpl(
    private val localDataSource: AppStorageRepository
) : TaskRepository {

    override fun getTasks(): Flow<Resource<List<Task>>> = flow {
        emit(Resource.Loading)
        try {
            val tasks = localDataSource.getTasks().filter { !it.isDeleted }
            emit(Resource.Success(tasks))
        } catch (e: Exception) {
            emit(Resource.Error(e))
        }
    }

    override suspend fun getTask(id: String): Resource<Task> {
        return try {
            val task = localDataSource.getTasks().find { it.id == id && !it.isDeleted }
            if (task != null) Resource.Success(task) else Resource.Error(Exception("Task not found"))
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override suspend fun saveTask(task: Task): Resource<Boolean> {
        return try {
            if (task.id.isEmpty()) {
                localDataSource.addTask(task)
            } else {
                localDataSource.updateTask(task)
            }
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override suspend fun deleteTask(id: String): Resource<Boolean> {
        return try {
            val task = localDataSource.getTasks().find { it.id == id }
            if (task != null) {
                localDataSource.updateTask(task.copy(isDeleted = true))
            }
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override suspend fun toggleTaskCompletion(id: String): Resource<Boolean> {
        return try {
            localDataSource.toggleTaskCompletion(id)
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    // Events
    override fun getEvents(): Flow<Resource<List<CalendarEvent>>> = flow {
        emit(Resource.Loading)
        try {
            val events = localDataSource.getEvents().filter { !it.isDeleted }
            emit(Resource.Success(events))
        } catch (e: Exception) {
            emit(Resource.Error(e))
        }
    }

    override suspend fun saveEvent(event: CalendarEvent): Resource<Boolean> {
        return try {
             // AppStorageRepository differentiates add/update usually by logic inside or explicit methods
             // storage.updateEvent checks ID, if exists updates. storage.addEvent adds. 
             // Logic in repo: 'updateEvent' handles logic: if index != -1 update else... wait, let's check AppStorageRepository
             if (localDataSource.getEvents().any { it.id == event.id }) {
                 localDataSource.updateEvent(event)
             } else {
                 localDataSource.addEvent(event)
             }
             Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override suspend fun deleteEvent(id: String): Resource<Boolean> {
        return try {
            val event = localDataSource.getEvents().find { it.id == id }
            if (event != null) {
                localDataSource.updateEvent(event.copy(isDeleted = true))
            }
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }
}
