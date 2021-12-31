package com.gib.filrouge.network

import com.gib.filrouge.tasklist.Task
import retrofit2.Response
import retrofit2.http.*

// This interface is used by Retrofit to make
// the right HTTP requests to the API.
interface TaskWebService {

    @GET("tasks")
    suspend fun getTasks(): Response<List<Task>>

    @POST("tasks")
    suspend fun createTask(@Body task: Task): Response<Task>

    @PATCH("tasks/{id}")
    suspend fun updateTask(@Body task: Task, @Path("id") id: String = task.id): Response<Task>

    @DELETE("tasks/{id}")
    suspend fun deleteTask(@Path("id") id: String): Response<Unit>

}