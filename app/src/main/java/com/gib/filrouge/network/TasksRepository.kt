package com.gib.filrouge.network

import com.gib.filrouge.tasklist.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class TasksRepository {

    private val tasksWebService = Api.tasksWebService

    suspend fun refresh(): List<Task>? {
        // Call HTTP (opération longue):
        val response = tasksWebService.getTasks()
        // À la ligne suivante, on a reçu la réponse de l'API:
        if (response.isSuccessful) {
            return response.body();
        }
        return null;
    }

    suspend fun deleteTask(task: Task): Boolean {
        val response = tasksWebService.deleteTask(task.id);
        if(response.isSuccessful) {
            return true;
        }
        return false;
    }

    suspend fun updateTask(task: Task): Task? {
        var response = tasksWebService.updateTask(task, task.id);
        if(response.isSuccessful) {
            return response.body();
        }
        return null;
    }

    suspend fun createTask(task: Task): Task? {
        var response = tasksWebService.createTask(task);
        if(response.isSuccessful) {
            return response.body();
        }
        return null;
    }

}