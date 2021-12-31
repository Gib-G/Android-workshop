package com.gib.filrouge.network

import com.gib.filrouge.tasklist.Task

// Makes HTTP requests to the API using the methods
// provided by the task web service.
class TaskRepository {

    private val taskWebService = Api.taskWebService

    suspend fun refresh(): List<Task>? {
        val response = taskWebService.getTasks()
        if (response.isSuccessful) {
            return response.body();
        }
        return null;
    }

    suspend fun deleteTask(task: Task): Boolean {
        val response = taskWebService.deleteTask(task.id);
        if(response.isSuccessful) {
            return true;
        }
        return false;
    }

    suspend fun updateTask(task: Task): Task? {
        var response = taskWebService.updateTask(task, task.id);
        if(response.isSuccessful) {
            return response.body();
        }
        return null;
    }

    suspend fun createTask(task: Task): Task? {
        var response = taskWebService.createTask(task);
        if(response.isSuccessful) {
            return response.body();
        }
        return null;
    }

}