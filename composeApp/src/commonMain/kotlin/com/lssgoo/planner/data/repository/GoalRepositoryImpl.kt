package com.lssgoo.planner.data.repository

import com.lssgoo.planner.data.local.AppStorageRepository
import com.lssgoo.planner.features.goals.models.Goal
import com.lssgoo.planner.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Valid implementation of GoalRepository that uses the existing JSON storage.
 * This bridges the new architecture with the old storage system.
 */
class GoalRepositoryImpl(
    private val localDataSource: AppStorageRepository
) : GoalRepository {

    override fun getGoals(): Flow<Resource<List<Goal>>> = flow {
        emit(Resource.Loading)
        try {
            // Apply soft-delete filter here
            val goals = localDataSource.getGoals().filter { !it.isDeleted }
            emit(Resource.Success(goals))
        } catch (e: Exception) {
            emit(Resource.Error(e, "Failed to load goals"))
        }
    }

    override suspend fun getGoal(id: String): Resource<Goal> {
        return try {
            val goal = localDataSource.getGoals().find { it.id == id && !it.isDeleted }
            if (goal != null) {
                Resource.Success(goal)
            } else {
                Resource.Error(Exception("Goal not found"))
            }
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override suspend fun saveGoal(goal: Goal): Resource<Boolean> {
        return try {
            localDataSource.updateGoal(goal)
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override suspend fun deleteGoal(id: String): Resource<Boolean> {
        return try {
            // Implement Soft Delete
            val existing = localDataSource.getGoals().find { it.id == id }
            if (existing != null) {
                // Update with isDeleted = true
                val deleted = existing.copy(isDeleted = true)
                localDataSource.updateGoal(deleted)
            }
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override suspend fun syncGoals(): Resource<Boolean> {
        // Future: Call API here
        return Resource.Success(true)
    }
}
