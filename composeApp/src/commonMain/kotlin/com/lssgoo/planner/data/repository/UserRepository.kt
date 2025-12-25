package com.lssgoo.planner.data.repository

import com.lssgoo.planner.data.model.UserProfile
import com.lssgoo.planner.util.Resource
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getUserProfile(): Flow<Resource<UserProfile>>
    suspend fun saveUserProfile(profile: UserProfile): Resource<Boolean>
}
