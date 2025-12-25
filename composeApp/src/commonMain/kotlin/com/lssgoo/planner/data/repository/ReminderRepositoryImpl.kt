package com.lssgoo.planner.data.repository

import com.lssgoo.planner.data.local.AppStorageRepository
import com.lssgoo.planner.features.reminders.models.Reminder
import com.lssgoo.planner.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ReminderRepositoryImpl(
    private val localDataSource: AppStorageRepository
) : ReminderRepository {

    override fun getReminders(): Flow<Resource<List<Reminder>>> = flow {
        emit(Resource.Loading)
        try {
            val reminders = localDataSource.getReminders().filter { !it.isDeleted }
            emit(Resource.Success(reminders))
        } catch (e: Exception) {
            emit(Resource.Error(e))
        }
    }

    override suspend fun saveReminder(reminder: Reminder): Resource<Boolean> {
        return try {
            if (localDataSource.getReminders().any { it.id == reminder.id }) {
                localDataSource.updateReminder(reminder)
            } else {
                localDataSource.addReminder(reminder)
            }
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override suspend fun deleteReminder(id: String): Resource<Boolean> {
        return try {
            val reminder = localDataSource.getReminders().find { it.id == id }
            if (reminder != null) {
                localDataSource.updateReminder(reminder.copy(isDeleted = true))
            }
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override suspend fun toggleReminderEnabled(id: String): Resource<Boolean> {
        return try {
            localDataSource.toggleReminderEnabled(id)
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }
}
