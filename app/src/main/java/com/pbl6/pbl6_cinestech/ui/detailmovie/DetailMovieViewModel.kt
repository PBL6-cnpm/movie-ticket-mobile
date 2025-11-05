package com.pbl6.pbl6_cinestech.ui.detailmovie

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.pbl6.pbl6_cinestech.data.model.response.MovieResponse
import com.pbl6.pbl6_cinestech.data.model.response.Response
import com.pbl6.pbl6_cinestech.data.repository.MovieRepository
import hoang.dqm.codebase.base.viewmodel.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

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