package com.pbl6.pbl6_cinestech.ui.refreshments

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.pbl6.pbl6_cinestech.data.model.request.ApplyRefreshmentsRequest
import com.pbl6.pbl6_cinestech.data.model.request.HoldingRequest
import com.pbl6.pbl6_cinestech.data.model.response.BookingResponse
import com.pbl6.pbl6_cinestech.data.model.response.ItemWrapper
import com.pbl6.pbl6_cinestech.data.model.response.RefreshmentsResponse
import com.pbl6.pbl6_cinestech.data.model.response.Response
import com.pbl6.pbl6_cinestech.data.repository.BookingRepository
import com.pbl6.pbl6_cinestech.data.repository.RefreshmentsRepository
import hoang.dqm.codebase.base.viewmodel.BaseViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class RefreshmentsViewModel(
    private val refreshmentsRepository: RefreshmentsRepository,
    private val bookingRepository: BookingRepository
): BaseViewModel() {
    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        getAllRefreshments()
    }
    private val _price: MutableLiveData<Int> = MutableLiveData(0)
    val price : LiveData<Int> get() = _price
    fun setPrice(value: Int){
        _price.value = value
    }
    fun add(price: Int){
        _price.value = _price.value?.plus(price)
    }
    fun minus(price: Int){
        _price.value = _price.value?.minus(price)
    }

    private val _allRefreshmentsResult = MutableStateFlow<Response<ItemWrapper<RefreshmentsResponse>>?>(null)
    val allRefreshmentsResult: MutableStateFlow<Response<ItemWrapper<RefreshmentsResponse>>?> = _allRefreshmentsResult
    val allRefreshmentsResultLiveData = allRefreshmentsResult.asLiveData()
    fun getAllRefreshments(){
        viewModelScope.launch {
            try {
                val response = refreshmentsRepository.getAllRefreshments()
                _allRefreshmentsResult.value = response
            }catch (e: Exception){

            }
        }
    }

    private val _applyRefreshmentsEvent  = MutableSharedFlow<Response<BookingResponse>>()
    val applyRefreshmentsEvent = _applyRefreshmentsEvent
    fun applyRefreshments(bookingRequest: ApplyRefreshmentsRequest){
        viewModelScope.launch {
            try {
                val response = bookingRepository.applyRefreshments(bookingRequest)
                _applyRefreshmentsEvent.emit(response)
            }catch (e: Exception){
                Log.e("check booking", "hold error: ${e.message}", e)

            }
        }
    }

    class RefreshmentsViewModelFactory(
        private val refreshmentsRepository: RefreshmentsRepository,
        private val bookingRepository: BookingRepository
    ): ViewModelProvider.Factory{
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(RefreshmentsViewModel::class.java)) {
                return RefreshmentsViewModel(refreshmentsRepository, bookingRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}