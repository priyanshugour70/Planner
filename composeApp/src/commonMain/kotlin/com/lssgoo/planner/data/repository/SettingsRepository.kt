package com.lssgoo.planner.data.repository

import com.lssgoo.planner.data.model.AppSettings
import com.lssgoo.planner.util.Resource
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun getSettings(): Flow<Resource<AppSettings>>
    suspend fun saveSettings(settings: AppSettings): Resource<Boolean>
}
