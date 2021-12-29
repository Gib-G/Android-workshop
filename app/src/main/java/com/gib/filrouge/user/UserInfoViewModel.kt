package com.gib.filrouge.user

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gib.filrouge.network.TasksRepository
import com.gib.filrouge.network.UserInfo
import com.gib.filrouge.tasklist.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class UserInfoViewModel: ViewModel() {

    private val repository = UserInfoRepository();

    private var _userInfo = MutableStateFlow<UserInfo?>(null);
    var userInfo = _userInfo.asStateFlow() ;

    fun refresh() {
        viewModelScope.launch {
            var data = repository.refresh();
            if(data != null) {
                _userInfo.value = data;
                userInfo = _userInfo;
            }
        }
    }

    fun updateAvatar(avatar: MultipartBody.Part) {
        viewModelScope.launch {
            var data = repository.updateAvatar(avatar);
            if(data != null) {
                _userInfo.value = data;
                userInfo = _userInfo;
            }
        }
    }

    fun update(user: UserInfo) {
        viewModelScope.launch {
            var data = repository.update(user);
            if(data != null) {
                _userInfo.value = data;
                userInfo = _userInfo;
            }
        }
    }

}