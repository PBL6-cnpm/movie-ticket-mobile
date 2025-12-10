package com.pbl6.pbl6_cinestech.ui.register

import androidx.lifecycle.viewModelScope
import com.pbl6.pbl6_cinestech.data.model.response.Response
import com.pbl6.pbl6_cinestech.data.model.response.UserData
import com.pbl6.pbl6_cinestech.data.repository.AuthRepository
import hoang.dqm.codebase.base.viewmodel.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : BaseViewModel() {

    private val _registerResult = MutableStateFlow<Response<UserData>?>(null)
    val registerResult: StateFlow<Response<UserData>?> = _registerResult

    private val _registerError = MutableStateFlow<String?>(null)
    val registerError: StateFlow<String?> = _registerError

    fun register(email: String, password: String, fullname: String) {
        viewModelScope.launch {
            try {
                val response = authRepository.register(email, password, fullname)
                if (response.success){
                    _registerResult.value = response
                }else{
                    _registerError.value = response.message
                }
            } catch (e: IllegalArgumentException) {
                _registerError.value = e.message
            } catch (e: Exception) {
                //toast
                _registerError.value = ""
            }
        }
    }
}