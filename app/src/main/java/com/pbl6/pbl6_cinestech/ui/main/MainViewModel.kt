package com.pbl6.pbl6_cinestech.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pbl6.pbl6_cinestech.data.model.response.MovieResponse
import hoang.dqm.codebase.ui.vm.BaseMainViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel : BaseMainViewModel() {
    private val _movieSelected = MutableStateFlow<MovieResponse?>(null)
    val currentMovieSelected = _movieSelected.asStateFlow()
    fun setMovieSelected(movie: MovieResponse){
        _movieSelected.value = movie
    }
    fun getMovieSelected(): MovieResponse {
        return _movieSelected.value ?: throw IllegalStateException("Current Movie is not set")
    }
    private val _isLogin = MutableLiveData(false)
    val isLoginLiveData: LiveData<Boolean> get() = _isLogin
    fun setLogin(isLogin: Boolean) {
        _isLogin.value = isLogin
    }

}