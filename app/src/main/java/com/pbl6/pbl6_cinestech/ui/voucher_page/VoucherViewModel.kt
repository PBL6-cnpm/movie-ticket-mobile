package com.pbl6.pbl6_cinestech.ui.voucher_page

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.pbl6.pbl6_cinestech.data.model.response.Response
import com.pbl6.pbl6_cinestech.data.model.response.VoucherResponse
import com.pbl6.pbl6_cinestech.data.repository.BookingRepository
import hoang.dqm.codebase.base.viewmodel.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class VoucherViewModel(
    private val bookingRepository: BookingRepository
): BaseViewModel() {
    private val _allVoucher = MutableStateFlow<Response<List<VoucherResponse>>?>(null)
    val allVoucher: MutableStateFlow<Response<List<VoucherResponse>>?> = _allVoucher
    val allVoucherLiveData = allVoucher.asLiveData()
    fun getAllVoucher(){
        viewModelScope.launch {
            try {
                val response = bookingRepository.getAllVoucher()
                _allVoucher.value = response
            }catch (e: Exception){
                Log.e("voucherViewModel", "Login error: ${e.message}", e)

            }
        }
    }

    class VoucherViewModelFactory(
        private val bookingRepository: BookingRepository
    ): ViewModelProvider.Factory{
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(VoucherViewModel::class.java)) {
                return VoucherViewModel(bookingRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

}