package com.pbl6.pbl6_cinestech.ui.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.pbl6.pbl6_cinestech.data.model.request.ChangePasswordRequest
import com.pbl6.pbl6_cinestech.data.model.request.UpdateProfileRequest
import com.pbl6.pbl6_cinestech.data.model.response.AccountResponse
import com.pbl6.pbl6_cinestech.data.model.response.Response
import com.pbl6.pbl6_cinestech.data.repository.ProfileRepository
import com.pbl6.pbl6_cinestech.ui.payment_information.PaymentInformationViewModel
import hoang.dqm.codebase.base.viewmodel.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    val profileRepository: ProfileRepository
): BaseViewModel() {
    private val _accountResult = MutableStateFlow<Response<AccountResponse>?>(null)
    val accountResult: MutableStateFlow<Response<AccountResponse>?> = _accountResult
    val accountResultLiveData = accountResult.asLiveData()
    fun updateProfile(updateProfileRequest: UpdateProfileRequest){
        viewModelScope.launch {
            try {
                val response = profileRepository.updateProfile(updateProfileRequest)
                _accountResult.value = response
            }catch (e: Exception){
                Log.e("LoginViewModel", "Login error: ${e.message}", e)
            }
        }
    }

    private val _updatePasswordResult = MutableStateFlow<Response<AccountResponse>?>(null)
    val updatePasswordResult: MutableStateFlow<Response<AccountResponse>?> = _updatePasswordResult
    val updatePasswordResultLiveData = updatePasswordResult.asLiveData()
    fun updatePassword(password: ChangePasswordRequest){
        viewModelScope.launch {
            try {
                val response = profileRepository.updatePassword(password)
                _updatePasswordResult.value = response
            }catch (e: Exception){
                Log.e("LoginViewModel", "Login error: ${e.message}", e)
            }
        }
    }


    class ProfileViewModelFactory(
        private val profileRepository: ProfileRepository
    ): ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
                return ProfileViewModel(profileRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}