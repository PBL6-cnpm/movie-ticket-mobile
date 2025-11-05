package com.pbl6.pbl6_cinestech

import androidx.core.content.ContextCompat
import com.makeramen.roundedimageview.BuildConfig
import com.pbl6.pbl6_cinestech.data.api.NetworkProvider
import hoang.dqm.codebase.base.application.BaseApplication
import hoang.dqm.codebase.data.AppInfo

class MainApplication: BaseApplication() {
    override val appInfo: AppInfo by lazy {
        AppInfo(
            appId = BuildConfig.APPLICATION_ID,
            icon = R.mipmap.ic_launcher,
            appName = ContextCompat.getContextForLanguage(this).getString(R.string.app_name),
        )
    }

    override fun onCreate() {
        super.onCreate()
        NetworkProvider.init(this)
    }
}