package com.pbl6.pbl6_cinestech.ui.detailbooking

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.pbl6.pbl6_cinestech.data.model.request.AuthEvent
import com.pbl6.pbl6_cinestech.data.model.response.BranchResponse
import com.pbl6.pbl6_cinestech.data.model.response.ItemWrapper
import com.pbl6.pbl6_cinestech.data.model.response.Response
import com.pbl6.pbl6_cinestech.data.model.response.ShowTimeResponse
import com.pbl6.pbl6_cinestech.data.repository.BranchRepository
import com.pbl6.pbl6_cinestech.data.repository.MovieRepository
import com.pbl6.pbl6_cinestech.data.repository.ShowTimeRepository
import hoang.dqm.codebase.base.viewmodel.BaseViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class DetailBookingViewModel(
    private val movieRepository: MovieRepository,
    private val branchRepository: BranchRepository,
    private val showTimeRepository: ShowTimeRepository
) : BaseViewModel() {
    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
//        getAllBranchResult()
    }

    private val _authEvent = MutableSharedFlow<AuthEvent>()
    val authEvent = _authEvent

    private val _branchBookingResult = MutableStateFlow<Response<BranchResponse>?>(null)
    val branchBookingResult: MutableStateFlow<Response<BranchResponse>?> = _branchBookingResult
    val branchBookingResultLiveData = branchBookingResult.asLiveData()
    fun getBranchWithMovieId(movieId: String) {
        viewModelScope.launch {
            try {
                val response = branchRepository.getBranchWithMovieId(movieId)
                _branchBookingResult.value = response
            } catch (e: Exception) {
            }
        }
    }

    private val _allBranchResult = MutableStateFlow<Response<List<BranchResponse>>?>(null)
    val allBranchResult: MutableStateFlow<Response<List<BranchResponse>>?> = _allBranchResult
    val allBranchResultLiveData = allBranchResult.asLiveData()
    fun getAllBranchResult() {
        viewModelScope.launch {
            try {
                val response = branchRepository.getAllBranch()
                _allBranchResult.value = response
            } catch (e: Exception) {
            }
        }
    }

    fun getAllBranchesWithMovieId(movieId: String){
        viewModelScope.launch {
            try {
                val response = branchRepository.getBranchesWithMovieId(movieId)
                _allBranchResult.value = response
            } catch (e: Exception) {
                Log.e("showTimeView", "detail error: ${e.message}", e)
            }
        }
    }

    private val _showTimeResponse = MutableStateFlow<Response<ItemWrapper<ShowTimeResponse>>?>(null)
    val showTimeResponse: MutableStateFlow<Response<ItemWrapper<ShowTimeResponse>>?> = _showTimeResponse
    val showTimeResponseLiveData = showTimeResponse.asLiveData()
    fun getShowTimeWithBranchAndMovie(movieId: String, branchId: String) {
        viewModelScope.launch {
            try {
                val response = showTimeRepository.getShowTimeWithBranchAndMovie(movieId, branchId)
                Log.d("BOOKING_JSON", "$response")

                _showTimeResponse.value = response
            }catch (e: HttpException) {
                if (e.code() == 401) {
                    Log.e("showTimeView", "Unauthorized - chưa đăng nhập hoặc token hết hạn")
                    _authEvent.emit(AuthEvent.RequireLogin)

                } else {
                    Log.e("showTimeView", "HTTP error ${e.code()}")
                }
            }
            catch (e: Exception){
                Log.e("showTimeView", "Login error: ${e.message}", e)
            }
        }
    }

    class DetailBookingViewModelFactory(
        private val movieRepository: MovieRepository,
        private val branchRepository: BranchRepository,
        private val showTimeRepository: ShowTimeRepository
    ): ViewModelProvider.Factory{
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(DetailBookingViewModel::class.java)) {
                return DetailBookingViewModel(movieRepository,branchRepository, showTimeRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

}