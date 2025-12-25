package com.lssgoo.planner.data.repository

import com.lssgoo.planner.features.reminders.models.Reminder
import com.lssgoo.planner.util.Resource
import kotlinx.coroutines.flow.Flow

interface ReminderRepository {
    fun getReminders(): Flow<Resource<List<Reminder>>>
    suspend fun saveReminder(reminder: Reminder): Resource<Boolean>
    suspend fun deleteReminder(id: String): Resource<Boolean>
    suspend fun toggleReminderEnabled(id: String): Resource<Boolean>
}
