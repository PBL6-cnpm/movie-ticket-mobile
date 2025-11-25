package com.pbl6.pbl6_cinestech.ui.payment_information

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.pbl6.pbl6_cinestech.data.model.request.ApplyVoucherRequest
import com.pbl6.pbl6_cinestech.data.model.request.BookingRequest
import com.pbl6.pbl6_cinestech.data.model.request.PaymentRequest
import com.pbl6.pbl6_cinestech.data.model.response.ApplyVoucherResponse
import com.pbl6.pbl6_cinestech.data.model.response.BookingResponse
import com.pbl6.pbl6_cinestech.data.model.response.BookingSeatResponse
import com.pbl6.pbl6_cinestech.data.model.response.PaymentResponse
import com.pbl6.pbl6_cinestech.data.model.response.Response
import com.pbl6.pbl6_cinestech.data.model.response.VoucherResponse
import com.pbl6.pbl6_cinestech.data.repository.BookingRepository
import com.pbl6.pbl6_cinestech.data.repository.PaymentRepository
import hoang.dqm.codebase.base.viewmodel.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class PaymentInformationViewModel(
    private val bookingRepository: BookingRepository,
    private val paymentRepository: PaymentRepository
): BaseViewModel() {
    private val _paymentResult = MutableStateFlow<Response<PaymentResponse>?>(null)
    val paymentResult: MutableStateFlow<Response<PaymentResponse>?> = _paymentResult
    val paymentResultLiveData = paymentResult.asLiveData()
    fun createPayment(paymentRequest: PaymentRequest) {
        viewModelScope.launch {
            try {
                val response = paymentRepository.createPaymentIntent(paymentRequest)
                _paymentResult.value = response
            } catch (e: Exception) {
                Log.e("LoginViewModel", "Login error: ${e.message}", e)
            }
        }
    }

    private val _applyVoucherResponse = MutableStateFlow<Response<ApplyVoucherResponse>?>(null)
    val applyVoucherResponse: MutableStateFlow<Response<ApplyVoucherResponse>?> = _applyVoucherResponse
    val applyVoucherResponseLiveData = applyVoucherResponse.asLiveData()
    fun applyVoucher(applyVoucherRequest: ApplyVoucherRequest){
        viewModelScope.launch {
            try {
                val response = bookingRepository.applyVoucher(applyVoucherRequest)
                _applyVoucherResponse.value = response
            }catch (e: Exception){
                Log.e("LoginViewModel", "Login error: ${e.message}", e)

            }
        }
    }
    private val _bookingSeatResult = MutableStateFlow<Response<BookingSeatResponse>?>(null)
    val bookingSeatResult = _bookingSeatResult
    val bookingResultLiveData = bookingSeatResult.asLiveData()
    fun bookingSeat(bookingRequest: BookingRequest) {
        viewModelScope.launch {
            try {
                Log.d("BOOKING_JSON", Gson().toJson(bookingRequest))
                val response = bookingRepository.bookingSeat(bookingRequest)
                Log.d("BOOKING_JSON", "$response")
                _bookingSeatResult.value = response
            } catch (e: Exception) {
                Log.e("LoginViewModel", "Login error: ${e.message}", e)
                _bookingSeatResult.value = Response(
                    success = false,
                    statusCode = 400,
                    message = "Oops! Your drawing reservation was cancelled because it exceeded the holding time. Please book again to continue.",
                    code = "BOOKING_EXPIRED",
                    data = null
                )
            }
        }
    }

    class PaymentInformationViewModelFactory(
        private val bookingRepository: BookingRepository,
        private val paymentRepository: PaymentRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(PaymentInformationViewModel::class.java)) {
                return PaymentInformationViewModel(bookingRepository, paymentRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}