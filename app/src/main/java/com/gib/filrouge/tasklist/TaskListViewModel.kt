package com.gib.filrouge.tasklist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gib.filrouge.network.TasksRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TaskListViewModel : ViewModel() {

    private val repository = TasksRepository();

    private val _taskList = MutableStateFlow<List<Task>>(emptyList());
    public val taskList: StateFlow<List<Task>> = _taskList;

    fun refresh() {
        viewModelScope.launch {
            var taskList = repository.refresh();
            if (taskList != null) {
                _taskList.value = taskList; }
        }
    }

    fun delete(task: Task) {
        viewModelScope.launch {
            if (repository.deleteTask(task)) {
                _taskList.value = taskList.value - task;
            }
        }
    }

    fun addOrEdit(task: Task) {
        viewModelScope.launch {
            val oldTask = taskList.value.firstOrNull { it.id == task.id }
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