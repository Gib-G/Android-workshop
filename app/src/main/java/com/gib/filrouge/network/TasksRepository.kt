package com.gib.filrouge.network

import com.gib.filrouge.tasklist.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class TasksRepository {

    private val tasksWebService = Api.tasksWebService

    // Ces deux variables encapsulent la même donnée:
    // [_taskList] est modifiable mais privée donc inaccessible à l'extérieur de cette classe
    private val _taskList = MutableStateFlow<List<Task>>(value = emptyList())
    // [taskList] est publique mais non-modifiable:
    // On pourra seulement l'observer (s'y abonner) depuis d'autres classes
    val taskList: StateFlow<List<Task>> = _taskList.asStateFlow()

    suspend fun refresh() {
        // Call HTTP (opération longue):
        val tasksResponse = tasksWebService.getTasks()
        // À la ligne suivante, on a reçu la réponse de l'API:
        if (tasksResponse.isSuccessful) {
            val fetchedTasks = tasksResponse.body()
            // on modifie la valeur encapsulée, ce qui va notifier ses Observers et donc déclencher leur callback
            if (fetchedTasks != null) _taskList.value = fetchedTasks
        }
    }

    suspend fun deleteTask(task: Task) {

        val response = tasksWebService.deleteTask(task.id);

        if(response.isSuccessful) {

            // If deletion is successful on the API, we effectively
            // remove the task from the local task repo.
            _taskList.value = taskList.value - task;

        }
    }

    suspend fun updateOrCreateTask(task: Task) {

        var response = tasksWebService.updateTask(task, task.id);

        if(response.isSuccessful) {

            val oldTask = _taskList.value.find { it.id == task.id; }
            if(oldTask != null) _taskList.value = taskList.value - oldTask;
            _taskList.value = taskList.value + task;

        } else {

            if(tasksWebService.createTask(task).isSuccessful) {

                _taskList.value = taskList.value + task;

            }

        }

    }

}