package com.pbl6.pbl6_cinestech.ui.home

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.pbl6.pbl6_cinestech.data.model.response.AccountResponse
import com.pbl6.pbl6_cinestech.data.model.response.ItemWrapper
import com.pbl6.pbl6_cinestech.data.model.response.MovieResponse
import com.pbl6.pbl6_cinestech.data.model.response.Response
import com.pbl6.pbl6_cinestech.data.model.response.ReviewResponse
import com.pbl6.pbl6_cinestech.data.repository.AuthRepository
import com.pbl6.pbl6_cinestech.data.repository.MovieRepository
import com.pbl6.pbl6_cinestech.data.repository.ReviewRepository
import hoang.dqm.codebase.base.viewmodel.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val movieRepository: MovieRepository,
    private val reviewRepository: ReviewRepository,
    private val authRepository: AuthRepository
) : BaseViewModel() {


    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        getMovieNowShowing(8, 0)
        getMovieUpComing(8, 0)
        getLatestReviews()
    }

    private val _movieNowShowingResult =
        MutableStateFlow<Response<ItemWrapper<MovieResponse>>?>(null)
    val movieNowShowingResult: MutableStateFlow<Response<ItemWrapper<MovieResponse>>?> =
        _movieNowShowingResult
    val movieNowShowingResultLiveData = movieNowShowingResult.asLiveData()
    fun getMovieNowShowing(limit: Int, offset: Int) {
        viewModelScope.launch {
            try {
                Log.d("check get movie", "getMovieNowShowing: ")
                val response = movieRepository.getAllMovieNowShowing(limit, offset)
                Log.d("check get movie", "getMovieNowShowing: $response")
                _movieNowShowingResult.value = response
            } catch (e: Exception) {
                Log.e("check get movie", "Error in getMovieNowShowing", e)
            }
        }
    }

    private val _movieUpcomingResult = MutableStateFlow<Response<ItemWrapper<MovieResponse>>?>(null)
    val movieUpcomingResult: MutableStateFlow<Response<ItemWrapper<MovieResponse>>?> =
        _movieUpcomingResult
    val movieUpComingResultLiveData = movieUpcomingResult.asLiveData()
    fun getMovieUpComing(limit: Int, offset: Int) {
        viewModelScope.launch {
            try {
                val response = movieRepository.getAllMovieUpcoming(limit, offset)
                _movieUpcomingResult.value = response
            } catch (e: Exception) {
            }
        }
    }

    private val _reviewResult = MutableStateFlow<Response<List<ReviewResponse>>?>(null)
    val reviewResult: MutableStateFlow<Response<List<ReviewResponse>>?> = _reviewResult
    val reviewResultLiveData = reviewResult.asLiveData()
    fun getLatestReviews() {
        viewModelScope.launch {
            try {
                val response = reviewRepository.getLatestReviews()
                _reviewResult.value = response
            } catch (e: Exception) {
            }
        }
    }
    private val _accountResult = MutableStateFlow<Response<AccountResponse>?>(null)
    val accountResult: MutableStateFlow<Response<AccountResponse>?> = _accountResult
    val accountResultLiveData = accountResult.asLiveData()
    fun getAccountResult(){
        viewModelScope.launch {
            try {
                val response = authRepository.getAccount()
                _accountResult.value = response
            }catch (e:Exception){
                Log.e("LoginViewModel", "Login error: ${e.message}", e)
            }
        }
    }

    class HomeViewModelFactory(
        private val movieRepository: MovieRepository,
        private val reviewRepository: ReviewRepository,
        private val authRepository: AuthRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
                return HomeViewModel(movieRepository, reviewRepository, authRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}