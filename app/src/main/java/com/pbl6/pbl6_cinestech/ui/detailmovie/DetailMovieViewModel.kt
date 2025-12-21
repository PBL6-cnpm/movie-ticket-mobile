package com.pbl6.pbl6_cinestech.ui.detailmovie

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.pbl6.pbl6_cinestech.data.model.request.AuthEvent
import com.pbl6.pbl6_cinestech.data.model.request.ReviewRequest
import com.pbl6.pbl6_cinestech.data.model.response.ItemWrapper
import com.pbl6.pbl6_cinestech.data.model.response.MovieResponse
import com.pbl6.pbl6_cinestech.data.model.response.Response
import com.pbl6.pbl6_cinestech.data.model.response.ReviewResponse
import com.pbl6.pbl6_cinestech.data.repository.MovieRepository
import hoang.dqm.codebase.base.viewmodel.BaseViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class DetailMovieViewModel(
    private val movieRepository: MovieRepository,
): BaseViewModel() {
    private val _movieDetailResult = MutableStateFlow<Response<MovieResponse>?>(null)
    val movieDetailResult: MutableStateFlow<Response<MovieResponse>?> = _movieDetailResult
    val movieDetailResultLiveData = movieDetailResult.asLiveData()
    fun getMovieDetail(id: String){
        viewModelScope.launch {
            try {
                val response = movieRepository.getMovieDetails(id)
                _movieDetailResult.value = response
            } catch (e: Exception) {
            }
        }
    }

    private val _allReview = MutableSharedFlow<Response<ItemWrapper<ReviewResponse>>>()
    val allReview = _allReview
    val allReviewLiveData = _allReview.asLiveData()
    fun getAllReview(id: String){
        viewModelScope.launch {
            try {
                val response = movieRepository.getAllReview(id)
                Log.d("BOOKING_JSON", "$response")
                _allReview.emit(response)
            } catch (e: Exception) {
                Log.e("LoginViewModel", "Login error: ${e.message}", e)
            }
        }
    }
    private val _authEvent = MutableSharedFlow<AuthEvent>()
    val authEvent = _authEvent
    private val _addReviewResult = MutableSharedFlow<Response<ReviewResponse>>()
    val addReviewResult = _addReviewResult
    fun addReview(reviewRequest: ReviewRequest){
        viewModelScope.launch {
            try {
                val response = movieRepository.addReview(reviewRequest)
                _addReviewResult.emit(response)
            }catch (e: HttpException) {
                if (e.code() == 401) {
                    Log.e("showTimeView", "Unauthorized - chưa đăng nhập hoặc token hết hạn")
                    _authEvent.emit(AuthEvent.RequireLogin)

                } else {
                    Log.e("showTimeView", "HTTP error ${e.code()}")
                }
            }catch (e: Exception){
            }
        }
    }

    fun deleteReview(id: String){
        viewModelScope.launch {
            try {
                movieRepository.deleteReview(id)
            } catch (e: Exception) {
            }
        }
    }

    class DetailViewModelFactory(
        private val movieRepository: MovieRepository,
    ): ViewModelProvider.Factory{
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(DetailMovieViewModel::class.java)) {
                return DetailMovieViewModel(movieRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}