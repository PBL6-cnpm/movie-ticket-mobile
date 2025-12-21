package com.pbl6.pbl6_cinestech.ui.seatbooking

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.pbl6.pbl6_cinestech.data.model.request.HoldingRequest
import com.pbl6.pbl6_cinestech.data.model.response.HoldingSeatResponse
import com.pbl6.pbl6_cinestech.data.model.response.Response
import com.pbl6.pbl6_cinestech.data.model.response.SeatBookingResponse
import com.pbl6.pbl6_cinestech.data.repository.BookingRepository
import com.pbl6.pbl6_cinestech.data.repository.SeatRepository
import hoang.dqm.codebase.base.viewmodel.BaseViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class SeatBookingViewModel(
    private val seatRepository: SeatRepository,
    private val bookingRepository: BookingRepository,
) : BaseViewModel() {


    private val _price: MutableLiveData<Int> = MutableLiveData(0)
    val price : LiveData<Int> get() = _price
    fun setPrice(value: Int){
        _price.value = value
    }
    fun addSeat(priceSeat:Int){
        _price.value = _price.value?.plus(priceSeat)
    }
    fun removeSeat(priceSeat: Int){
        _price.value = _price.value?.minus(priceSeat)
    }
    private val _allSeatResult = MutableStateFlow<Response<SeatBookingResponse>?>(null)
    val allSeatResult: MutableStateFlow<Response<SeatBookingResponse>?> = _allSeatResult
    val allSeatResultLiveData = allSeatResult.asLiveData()
    fun getAllSeatByShowTimeId(showTimeId: String) {
        viewModelScope.launch {
            try {
                val response = seatRepository.getSeatWithShowTime(showTimeId)
                _allSeatResult.value = response
            } catch (e: Exception) {
                Log.e("check seat", "seat error: ${e.message}", e)
            }
        }
    }

//    private val _holdSeatResult = MutableStateFlow<Response<HoldingSeatResponse>?>(null)
//    val holdSeatResult: MutableStateFlow<Response<HoldingSeatResponse>?> = _holdSeatResult
//    val holdSeatResultLiveData = holdSeatResult.asLiveData()
    private val _holdSeatResult = MutableSharedFlow<Response<HoldingSeatResponse>>()
    val holdSeatResult = _holdSeatResult
    fun holdSeat(bookingRequest: HoldingRequest){
        viewModelScope.launch {
            try {
                val response = bookingRepository.holdSeat(bookingRequest)
                _holdSeatResult.emit(response)
            }catch (e: HttpException) {
                when (e.code()) {
                    409 -> {
                        val errorBody = e.response()?.errorBody()?.string()
                        Log.e("API_ERROR", "Conflict: $errorBody")
                        _holdSeatResult.emit(Response(
                            success = false,
                            statusCode = 400,
                            message = "Oops! This seat has already been held or booked by someone else. Please choose another seat.",
                            code = "BOOKING_EXPIRED",
                            data = null
                        ))
                    }
                    401 -> {
                        Log.e("API_ERROR", "Other HTTP error: ${e.code()}")
                    }
                }
            }
            catch (e: Exception){
                Log.e("check hold", "hold error: ${e.message}", e)

            }
        }
    }

    class SeatBookingViewModelFactory(
        private val seatRepository: SeatRepository,
        private val bookingRepository: BookingRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SeatBookingViewModel::class.java)) {
                return SeatBookingViewModel(seatRepository, bookingRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

}