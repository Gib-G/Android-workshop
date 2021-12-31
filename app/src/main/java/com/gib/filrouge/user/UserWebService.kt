package com.gib.filrouge.user

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

// This interface is used by Retrofit to make
// the right user-related HTTP requests to the API.
interface UserWebService {

    @GET("users/info")
    suspend fun getInfo(): Response<UserInfo>

    @Multipart
    @PATCH("users/update_avatar")
    suspend fun updateAvatar(@Part avatar: MultipartBody.Part): Response<UserInfo>

    @PATCH("users")
    suspend fun update(@Body user: UserInfo): Response<UserInfo>

    @POST("users/login")
    suspend fun login(@Body loginDetails: LoginForm): Response<AuthenticationResponse>

    @POST("users/sign_up")
    suspend fun signUp(@Body signupDetails: SignUpForm): Response<AuthenticationResponse>

}