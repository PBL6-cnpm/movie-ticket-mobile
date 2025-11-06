package com.pbl6.pbl6_cinestech.ui.refreshments

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.pbl6.pbl6_cinestech.data.model.response.ItemWrapper
import com.pbl6.pbl6_cinestech.data.model.response.RefreshmentsResponse
import com.pbl6.pbl6_cinestech.data.model.response.Response
import com.pbl6.pbl6_cinestech.data.repository.RefreshmentsRepository
import hoang.dqm.codebase.base.viewmodel.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class RefreshmentsViewModel(
    private val refreshmentsRepository: RefreshmentsRepository
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

    class RefreshmentsViewModelFactory(
        private val refreshmentsRepository: RefreshmentsRepository
    ): ViewModelProvider.Factory{
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(RefreshmentsViewModel::class.java)) {
                return RefreshmentsViewModel(refreshmentsRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}