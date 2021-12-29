package com.gib.filrouge.user

import android.net.Uri
import com.gib.filrouge.network.Api
import com.gib.filrouge.network.UserInfo
import okhttp3.MultipartBody

class UserInfoRepository {

    private val userWebService = Api.userWebService;

    suspend fun refresh(): UserInfo? {
        var response = userWebService.getInfo();
        if(response.isSuccessful) {
            return response.body();
        }
        return null;
    }

    suspend fun updateAvatar(avatar: MultipartBody.Part): UserInfo? {
        var response = userWebService.updateAvatar(avatar);
        if(response.isSuccessful) {
            return response.body();
        }
        return null;
    }

    suspend fun update(user: UserInfo): UserInfo? {
        var response = userWebService.update(user);
        if(response.isSuccessful) {
            return response.body();
        }
        return null;
    }

}