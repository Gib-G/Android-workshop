package com.gib.filrouge.network

import com.gib.filrouge.user.LoginForm
import com.gib.filrouge.user.AuthenticationResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface UserWebService {

    // "suspend" means this function takes time to execute
    // (whatever that means...).
    @GET("users/info")
    suspend fun getInfo(): Response<UserInfo>

    @Multipart
    @PATCH("users/update_avatar")
    suspend fun updateAvatar(@Part avatar: MultipartBody.Part): Response<UserInfo>

    @PATCH("users")
    suspend fun update(@Body user: UserInfo): Response<UserInfo>

    @POST("users/login")
    suspend fun login(@Body user: LoginForm): Response<AuthenticationResponse>

}