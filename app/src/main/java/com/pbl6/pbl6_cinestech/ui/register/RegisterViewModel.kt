package com.pbl6.pbl6_cinestech.ui.register

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresExtension
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.pbl6.pbl6_cinestech.data.model.response.Response
import com.pbl6.pbl6_cinestech.data.model.response.UserData
import com.pbl6.pbl6_cinestech.data.repository.AuthRepository
import hoang.dqm.codebase.base.viewmodel.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.IOException

class RegisterViewModel(
    private val authRepository: AuthRepository
) : BaseViewModel() {

    private val _registerResult = MutableStateFlow<Response<UserData>?>(null)
    val registerResult: StateFlow<Response<UserData>?> = _registerResult
    val registerResultLiveData = _registerResult.asLiveData()

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    fun register(email: String, password: String, fullname: String) {
        viewModelScope.launch {
            try {
                val response = authRepository.register(email, password, fullname)
                    _registerResult.value = response
            }catch (e: retrofit2.HttpException) {
                val errorResponse = parseErrorResponse(e)
                _registerResult.value = errorResponse
            } catch (e: IOException) {
                _registerResult.value = Response(
                    success = false,
                    statusCode = -1,
                    message = "Lỗi kết nối mạng, vui lòng thử lại.",
                    code = "NETWORK_ERROR",
                    data = null
                )
            } catch (e: Exception) {
                _registerResult.value = Response(
                    success = false,
                    statusCode = 400,
                    message = e.message?:"",
                    code = "${e.message}",
                    data = null
                )

            }
        }
    }

    private fun parseErrorResponse(e: retrofit2.HttpException): Response<UserData>? {
        return try {
            val errorBody = e.response()?.errorBody()?.string()
            if (errorBody != null) {
                val gson = Gson()
                val type = object : TypeToken<Response<UserData>>() {}.type
                gson.fromJson(errorBody, type)
            } else {
                Response(
                    success = false,
                    statusCode = e.code(),
                    message = "Lỗi không xác định từ server.",
                    code = "NO_ERROR_BODY",
                    data = null
                )
            }
        } catch (exception: Exception) {
            Log.e("RegisterViewModel", "Error parsing error body", exception)
            null
        }
    }

    class RegisterViewModelFactory(
        private val authRepository: AuthRepository
    ): ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
                return RegisterViewModel(authRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}