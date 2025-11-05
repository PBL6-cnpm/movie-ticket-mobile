package com.pbl6.pbl6_cinestech.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import hoang.dqm.codebase.ui.vm.BaseMainViewModel

class MainViewModel : BaseMainViewModel() {
    private val _isLogin = MutableLiveData(false)
    val isLoginLiveData: LiveData<Boolean> get() = _isLogin
    fun setLogin(isLogin: Boolean) {
        _isLogin.value = isLogin
    }

}