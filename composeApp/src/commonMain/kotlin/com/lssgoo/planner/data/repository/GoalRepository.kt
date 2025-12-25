package com.lssgoo.planner.data.repository

import com.lssgoo.planner.features.goals.models.Goal
import com.lssgoo.planner.util.Resource
import kotlinx.coroutines.flow.Flow

/**
 * Interface that abstracts the data source.
 * In the future, the implementation can stick to a local DB or sync with a remote API.
 */
interface GoalRepository {
    fun getGoals(): Flow<Resource<List<Goal>>>
    suspend fun getGoal(id: String): Resource<Goal>
    suspend fun saveGoal(goal: Goal): Resource<Boolean>
    suspend fun deleteGoal(id: String): Resource<Boolean>
    suspend fun syncGoals(): Resource<Boolean>
}
