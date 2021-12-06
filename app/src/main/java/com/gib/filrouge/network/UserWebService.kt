package com.gib.filrouge.network

import retrofit2.Response
import retrofit2.http.GET

interface UserWebService {

    // "suspend" means this function takes time to execute
    // (whatever that means...).
    @GET("users/info")
    suspend fun getInfo(): Response<UserInfo>

}