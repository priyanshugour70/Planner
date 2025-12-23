package com.lssgoo.planner.ui.viewmodel

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.lssgoo.planner.data.local.LocalStorageManager
import com.lssgoo.planner.data.model.Task
import com.lssgoo.planner.data.repository.TaskRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for Tasks feature - follows SRP and 200-300 lines rule
 */
class TasksViewModel(application: Application) : BaseViewModel(application) {
    
    private val storageManager = LocalStorageManager(application)
    private val taskRepository = TaskRepository(storageManager)
    
    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()
    
    init {
        loadTasks()
    }
    
    fun loadTasks() {
        viewModelScope.launch(Dispatchers.IO) {
            _tasks.value = taskRepository.getTasks()
        }
    }
    
    fun addTask(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            taskRepository.saveTask(task)
            loadTasks()
        }
    }
    
    fun updateTask(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            taskRepository.saveTask(task)
            loadTasks()
        }
    }
    
    fun deleteTask(taskId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            taskRepository.deleteTask(taskId)
            loadTasks()
        }
    }
    
    fun toggleTaskCompletion(taskId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            taskRepository.toggleTaskCompletion(taskId)
            loadTasks()
        }
    }
    
    fun getTasksForDate(date: Long): List<Task> {
        return _tasks.value.filter { task ->
            // Use similar logic to repository or expose via state
            true // Simplified for now
        }
    }
}
