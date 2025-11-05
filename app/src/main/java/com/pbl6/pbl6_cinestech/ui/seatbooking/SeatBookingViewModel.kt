package com.pbl6.pbl6_cinestech.ui.seatbooking

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.pbl6.pbl6_cinestech.data.model.response.Response
import com.pbl6.pbl6_cinestech.data.model.response.SeatBookingResponse
import com.pbl6.pbl6_cinestech.data.repository.SeatRepository
import hoang.dqm.codebase.base.viewmodel.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class SeatBookingViewModel(
    private val seatRepository: SeatRepository
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
            }
        }
    }

    class SeatBookingViewModelFactory(
        private val seatRepository: SeatRepository,
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SeatBookingViewModel::class.java)) {
                return SeatBookingViewModel(seatRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

}