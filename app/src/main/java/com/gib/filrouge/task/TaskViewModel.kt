package com.gib.filrouge.task

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// Task-related view model.
// It manages the logic of the task list and
// stores it as a state flow.
class TaskViewModel : ViewModel() {

    // The task repository doing the HTTP requests to the API.
    private val repository = TaskRepository()

    // The user's task list stored as state flows.
    private val _taskList = MutableStateFlow<List<Task>>(emptyList())
    val taskList: StateFlow<List<Task>> = _taskList

    fun refresh() {
        viewModelScope.launch {
            // Retrieves the list of tasks from the API.
            var taskList = repository.refresh()
            // TaskRepository::refresh returns null if the HTTP request failed.
            if (taskList != null) {
                _taskList.value = taskList
            }
        }
    }

    fun delete(task: Task) {
        viewModelScope.launch {
            // TaskRepository::deleteTask returns false if the HTTP request failed.
            if (repository.deleteTask(task)) {
                _taskList.value = taskList.value - task;
            }
        }
    }

    fun addOrEdit(task: Task) {
        viewModelScope.launch {
            val oldTask = taskList.value.firstOrNull { it.id == task.id }
            // Based on whether the task already exists in the list,
            // we update or create it in the API.
            val task = when {
                oldTask != null -> repository.updateTask(task);
                else -> repository.createTask(task);
            }
            if(task != null) {
                if(oldTask != null) {
                    _taskList.value = taskList.value - oldTask;
                }
                _taskList.value = taskList.value + task;
            }
        }
    }

}