package com.example.storyapp.viemodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.storyapp.api.ApiConfig
import com.example.storyapp.models.LoginRequest
import com.example.storyapp.models.LoginResponse
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private val _loginResult = MutableLiveData<Result<LoginResponse>>()
    val loginResult: LiveData<Result<LoginResponse>> = _loginResult

    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                val loginRequest = LoginRequest(email, password)
                // Now no token is passed here
                val response = ApiConfig.getApiService().login(loginRequest)

                if (response.isSuccessful && response.body() != null && !response.body()!!.error!!) {
                    _loginResult.value = Result.success(response.body()!!)
                } else {
                    _loginResult.value = Result.failure(Exception("Login failed"))
                }
            } catch (e: Exception) {
                _loginResult.value = Result.failure(e)
            }
        }
    }
}