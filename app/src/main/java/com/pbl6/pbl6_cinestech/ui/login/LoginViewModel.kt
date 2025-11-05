package com.pbl6.pbl6_cinestech.ui.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.pbl6.pbl6_cinestech.data.model.response.LoginResponse
import com.pbl6.pbl6_cinestech.data.model.response.Response
import com.pbl6.pbl6_cinestech.data.repository.AuthRepository
import hoang.dqm.codebase.base.viewmodel.BaseViewModel
import kotlinx.coroutines.launch


class LoginViewModel(
    private val authRepository: AuthRepository
) : BaseViewModel() {
    private val _loginResult = MutableLiveData<Response<LoginResponse>?>(null)
    val loginResultLiveData: LiveData<Response<LoginResponse>?> = _loginResult


    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                val response = authRepository.login(email, password)
                _loginResult.value = response
            } catch (e: Exception) {
                Log.e("LoginViewModel", "Login error: ${e.message}", e)
                _loginResult.value = Response(
                    success = false,
                    statusCode = 400,
                    message = "Invalid credentials",
                    code = "INVALID_CREDENTIALS",
                    data = null
                )
            }
        }
    }

    class LoginViewModelFactory(
        private val authRepository: AuthRepository
    ): ViewModelProvider.Factory{
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
                return LoginViewModel(authRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}