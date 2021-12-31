package com.gib.filrouge.network

import android.content.Context
import android.preference.PreferenceManager
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit

// Object for communicating with the API.
object Api {

    // The context of the app.
    lateinit var appContext: Context

    // Called when the app is launched (cf. App::onCreate).
    fun setUpContext(context: Context) {
        appContext = context
    }

    private const val BASE_URL = "https://android-tasks-api.herokuapp.com/api/"

    // HTTP client.
    private val okHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor { chain ->
                // This adds the user's API token to any
                // request to the API.
                // The user's token is retrieved from shared preferences
                // (key "auth_token_key").
                val newRequest = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer ${PreferenceManager.getDefaultSharedPreferences(appContext).getString("auth_token_key", "")}")
                    .build()
                chain.proceed(newRequest)
            }
            .build()
    }

    // JSON serializer.
    private val jsonSerializer = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    // Converter parsing the JSON API responses.
    private val converterFactory =
        jsonSerializer.asConverterFactory("application/json".toMediaType())

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(converterFactory)
        .build()

    // Service for user-related queries to the API.
    val userWebService: UserWebService by lazy {
        retrofit.create(UserWebService::class.java)
    }

    // Service for task-related queries to the API.
    val taskWebService: TaskWebService by lazy {
        retrofit.create(TaskWebService::class.java)
    }

}