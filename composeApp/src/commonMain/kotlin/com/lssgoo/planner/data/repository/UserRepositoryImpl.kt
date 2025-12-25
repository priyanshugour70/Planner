package com.lssgoo.planner.data.repository

import com.lssgoo.planner.data.local.AppStorageRepository
import com.lssgoo.planner.data.model.UserProfile
import com.lssgoo.planner.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class UserRepositoryImpl(
    private val localDataSource: AppStorageRepository
) : UserRepository {

    override fun getUserProfile(): Flow<Resource<UserProfile>> = flow {
        emit(Resource.Loading)
        try {
            val profile = localDataSource.getUserProfile() ?: UserProfile()
            emit(Resource.Success(profile))
        } catch (e: Exception) {
            emit(Resource.Error(e))
        }
    }

    override suspend fun saveUserProfile(profile: UserProfile): Resource<Boolean> {
        return try {
            localDataSource.saveUserProfile(profile)
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }
}
