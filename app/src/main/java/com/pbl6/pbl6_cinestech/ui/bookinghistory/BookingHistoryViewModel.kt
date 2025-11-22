package com.pbl6.pbl6_cinestech.ui.bookinghistory

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.pbl6.pbl6_cinestech.data.model.response.BookingHistoryResponse
import com.pbl6.pbl6_cinestech.data.model.response.ItemWrapper
import com.pbl6.pbl6_cinestech.data.model.response.Response
import com.pbl6.pbl6_cinestech.data.repository.BookingRepository
import com.pbl6.pbl6_cinestech.ui.detailbooking.DetailBookingViewModel
import hoang.dqm.codebase.base.viewmodel.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class BookingHistoryViewModel(
    private val bookingRepository: BookingRepository,
) : BaseViewModel() {
    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        getBookings()
    }

    private val _historyResult =
        MutableStateFlow<Response<ItemWrapper<BookingHistoryResponse>>?>(null)
    val historyResult = _historyResult
    val historyResultLiveData = historyResult.asLiveData()
    fun getBookings() {
        viewModelScope.launch {
            try {
                val response = bookingRepository.getBookings(10, 0)
                _historyResult.value = response
            } catch (e: Exception) {
            }
        }
    }

    class BookingHistoryFactory(
        private val bookingRepository: BookingRepository,
    ): ViewModelProvider.Factory{
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(BookingHistoryViewModel::class.java)) {
                return BookingHistoryViewModel(bookingRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}