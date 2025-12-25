package com.lssgoo.planner.data.repository

import com.lssgoo.planner.data.local.AppStorageRepository
import com.lssgoo.planner.data.model.AppSettings
import com.lssgoo.planner.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SettingsRepositoryImpl(
    private val localDataSource: AppStorageRepository
) : SettingsRepository {

    override fun getSettings(): Flow<Resource<AppSettings>> = flow {
        // AppStorageRepository doesn't expose Flow for settings yet, it's state based in VM?
        // Actually AppStorageRepository uses MultiplatformSettings.
        // We can emit current value. 
        // Real reactive settings might require a callback or polling if changed elsewhere,
        // but for now, we just fetch. 
        // If VM holds state, VM updates it.
        emit(Resource.Loading)
        try {
            val settings = localDataSource.getSettings()
            emit(Resource.Success(settings))
        } catch (e: Exception) {
            emit(Resource.Error(e))
        }
    }

    override suspend fun saveSettings(settings: AppSettings): Resource<Boolean> {
        return try {
            localDataSource.saveSettings(settings)
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }
}
