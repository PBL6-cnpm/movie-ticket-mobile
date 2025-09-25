package com.pbl6.pbl6_cinestech.ui.main

import com.pbl6.pbl6_cinestech.R
import hoang.dqm.codebase.databinding.ActivityMainBinding
import hoang.dqm.codebase.ui.features.main.BaseMainActivity

class MainActivity : BaseMainActivity<ActivityMainBinding, MainViewModel>() {
    override val graphResId: Int
        get() = R.navigation.app_nav

    override fun initData() {
        TODO("Not yet implemented")
    }
}