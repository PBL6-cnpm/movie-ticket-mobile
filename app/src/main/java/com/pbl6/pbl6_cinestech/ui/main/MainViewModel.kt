package com.pbl6.pbl6_cinestech.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pbl6.pbl6_cinestech.data.model.request.Refreshments
import com.pbl6.pbl6_cinestech.data.model.response.AccountResponse
import com.pbl6.pbl6_cinestech.data.model.response.BranchResponse
import com.pbl6.pbl6_cinestech.data.model.response.MovieResponse
import com.pbl6.pbl6_cinestech.data.model.response.ShowTimeResponse
import com.pbl6.pbl6_cinestech.data.model.response.VoucherResponse
import hoang.dqm.codebase.ui.vm.BaseMainViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel : BaseMainViewModel() {
    private val _showTimeResponse = MutableLiveData<ShowTimeResponse>()
    val showTimeResponse: LiveData<ShowTimeResponse> get() = _showTimeResponse
    fun setShowTimeResponse(value: ShowTimeResponse){
        _showTimeResponse.value = value
    }

    private val _movieSelected = MutableStateFlow<MovieResponse?>(null)
    val currentMovieSelected = _movieSelected.asStateFlow()
    fun setMovieSelected(movie: MovieResponse){
        _movieSelected.value = movie
    }
    fun getMovieSelected(): MovieResponse {
        return _movieSelected.value ?: throw IllegalStateException("Current Movie is not set")
    }
    private val _branchSelected = MutableStateFlow<BranchResponse?>(null)
    fun setBranchSelected(branch: BranchResponse){
        _branchSelected.value = branch
    }
    fun getBranchSelected(): BranchResponse {
        return _branchSelected.value ?: throw IllegalStateException("Current Branch is not set")
    }
    private val _listRefreshments = MutableLiveData<List<Refreshments>>()
    val listRefreshments: LiveData<List<Refreshments>> get() = _listRefreshments
    fun setListRefreshments(list: List<Refreshments>){
        _listRefreshments.value = list
    }
    fun getListRefreshments(): List<Refreshments> {
        return _listRefreshments.value ?: emptyList()
    }
    private val _voucherSelected = MutableLiveData<VoucherResponse?>()
    val voucherSelected: LiveData<VoucherResponse?> get() = _voucherSelected
    fun setVoucherSelected(voucher: VoucherResponse?){
        _voucherSelected.value = voucher
    }


    private val _account = MutableLiveData<AccountResponse?>()
    val account: LiveData<AccountResponse?> get() = _account
    fun setAccount(account: AccountResponse?){
        _account.value = account
    }
    fun getAccount(): AccountResponse {
        return _account.value ?: throw IllegalStateException("Current Branch is not set")
    }
    private val _isLogin = MutableLiveData(false)
    val isLoginLiveData: LiveData<Boolean> get() = _isLogin
    fun setLogin(isLogin: Boolean) {
        _isLogin.value = isLogin
    }

}