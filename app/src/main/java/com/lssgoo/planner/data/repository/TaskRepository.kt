package com.lssgoo.planner.data.repository

import com.lssgoo.planner.data.local.LocalStorageManager
import com.lssgoo.planner.data.model.Task
import java.util.Calendar

/**
 * Repository for managing Tasks - part of the backend layer
 */
class TaskRepository(private val storage: LocalStorageManager) {

    fun getTasks(): List<Task> {
        return storage.getTasks()
    }

    fun saveTask(task: Task) {
        val tasks = storage.getTasks().toMutableList()
        val index = tasks.indexOfFirst { it.id == task.id }
        if (index != -1) {
            tasks[index] = task.copy(updatedAt = System.currentTimeMillis())
        } else {
            tasks.add(0, task)
        }
        storage.saveTasks(tasks)
    }

    fun deleteTask(taskId: String) {
        val tasks = storage.getTasks().filter { it.id != taskId }
        storage.saveTasks(tasks)
    }

    fun toggleTaskCompletion(taskId: String) {
        val tasks = storage.getTasks().toMutableList()
        val index = tasks.indexOfFirst { it.id == taskId }
        if (index != -1) {
            val task = tasks[index]
            tasks[index] = task.copy(
                isCompleted = !task.isCompleted,
                completedAt = if (!task.isCompleted) System.currentTimeMillis() else null,
                updatedAt = System.currentTimeMillis()
            )
            storage.saveTasks(tasks)
        }
    }

    fun getTasksForDate(date: Long): List<Task> {
        val dayStart = getStartOfDay(date)
        val dayEnd = dayStart + (24 * 60 * 60 * 1000L) - 1L
        return storage.getTasks().filter { task ->
            task.dueDate?.let { it in dayStart..dayEnd } ?: false
        }
    }

    private fun getStartOfDay(timestamp: Long): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }
}
