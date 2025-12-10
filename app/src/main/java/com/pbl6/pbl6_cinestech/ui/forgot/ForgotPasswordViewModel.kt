package com.pbl6.pbl6_cinestech.ui.forgot

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.pbl6.pbl6_cinestech.data.model.response.Response
import com.pbl6.pbl6_cinestech.data.repository.AuthRepository
import com.pbl6.pbl6_cinestech.ui.login.LoginViewModel
import hoang.dqm.codebase.base.viewmodel.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class ForgotPasswordViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : BaseViewModel() {
    private val _forgotPasswordResult = MutableStateFlow<Response<Nothing>?>(null)
    val forgotPasswordResult: StateFlow<Response<Nothing>?> = _forgotPasswordResult
    val forgotPasswordResultLiveData = forgotPasswordResult.asLiveData()
    fun forgotPassword(email: String) {
        viewModelScope.launch {
            try {
                val response = authRepository.forgotPassword(email)
                _forgotPasswordResult.value = response
            }catch (e: Exception){
                Log.e("ForgotPasswordViewModel", "ForgotPassword error: ${e.message}", e)
            }
        }
    }

    class ForgotPasswordViewModelFactory(
        private val authRepository: AuthRepository
    ): ViewModelProvider.Factory{
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ForgotPasswordViewModel::class.java)) {
                return ForgotPasswordViewModel(authRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}