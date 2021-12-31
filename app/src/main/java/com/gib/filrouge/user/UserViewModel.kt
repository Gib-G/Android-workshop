package com.gib.filrouge.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gib.filrouge.network.UserInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

class UserViewModel: ViewModel() {

    private val repository = UserRepository();

    private var _userInfo = MutableStateFlow<UserInfo?>(null);
    var userInfo = _userInfo.asStateFlow() ;

    var authenticationResponse: AuthenticationResponse? = null;

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

    fun login(loginDetails: LoginForm) {
        viewModelScope.launch {
            authenticationResponse = repository.login(loginDetails)
        }
    }

    fun signUp(signupDetails: SignUpForm) {
        viewModelScope.launch {
            authenticationResponse = repository.signUp(signupDetails)
        }
    }

}