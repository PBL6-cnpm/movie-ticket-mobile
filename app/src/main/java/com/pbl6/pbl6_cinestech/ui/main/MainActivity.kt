package com.pbl6.pbl6_cinestech.ui.main

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.pbl6.pbl6_cinestech.R
import com.pbl6.pbl6_cinestech.data.api.NetworkProvider
import com.pbl6.pbl6_cinestech.data.model.request.RefreshTokenRequest
import com.pbl6.pbl6_cinestech.data.model.request.TokenPair
import com.pbl6.pbl6_cinestech.utils.JwtExpiration
import com.pbl6.pbl6_cinestech.utils.SecurePrefs
import hoang.dqm.codebase.base.activity.navigate
import hoang.dqm.codebase.databinding.ActivityMainBinding
import hoang.dqm.codebase.event.subscribeEventNetwork
import hoang.dqm.codebase.service.sound.AppMusicPlayer
import hoang.dqm.codebase.ui.features.main.BaseMainActivity
import hoang.dqm.codebase.utils.openSettingNetWork
import hoang.dqm.codebase.utils.singleClick
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : BaseMainActivity<ActivityMainBinding, MainViewModel>() {
    override val graphResId: Int
        get() = R.navigation.app_nav

    @RequiresApi(Build.VERSION_CODES.O)
    override fun initView() {
        super.initView()
        navController?.addOnDestinationChangedListener { _, destination, _ ->
            AppMusicPlayer.checkAndPlay()
        }
        binding.root.post {
            val destinationId = navController?.currentDestination?.id
            if (destinationId != R.id.homeFragment) {
                navigate(R.id.homeFragment, isPopAll = true)
            }
        }
        lifecycleScope.launch {
            try {
                val accessToken = SecurePrefs.getAccessToken(this@MainActivity)
                val refreshToken = SecurePrefs.getRefreshToken(this@MainActivity)

                if (accessToken != null && refreshToken != null) {
                    val tokens = TokenPair(accessToken, refreshToken)

                    val isValid = withContext(Dispatchers.IO) {
                        isValidAccessToken(tokens)
                    }
                    viewModel.setLogin(isValid)
                } else {
                    viewModel.setLogin(false)
                }

            } catch (e: CancellationException) {
                Log.w("MainActivity", "Coroutine bị hủy: ${e.message}")
                throw e
            } catch (e: Exception) {
                Log.e("MainActivity", "Lỗi khi kiểm tra token", e)
                viewModel.setLogin(false)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun isValidAccessToken(tokens: TokenPair): Boolean {
        Log.d("MainActivity", "Token hien tai: ${tokens.accessToken}")
        Log.d("MainActivity", "Token hien tai: ${tokens.refreshToken}")


        return if (JwtExpiration.isTokenExpired(tokens.accessToken)) {
            Log.d("MainActivity", "Token hết hạn: ${tokens.accessToken}")

            try {
                val authApi = NetworkProvider.authApiService
                val newToken = authApi.refreshToken(RefreshTokenRequest(tokens.refreshToken))
                Log.d("MainActivity", "Token mới: ${newToken}")
                if (newToken.data != null) {
                    SecurePrefs.saveTokens(
                        this, newToken.data.accessToken, newToken.data.refreshToken
                    )
                    Log.d(
                        "MainActivity",
                        "Token mới: ${newToken.data.accessToken} ${newToken.data.refreshToken}"
                    )
                    true
                } else {
                    null
                    false
                }
            } catch (e: Exception) {
                Log.e("MainActivity", "Lỗi khi kiểm tra token ${e.message}")
                return false
            }

        } else {
            Log.w("MainActivity", "Coroutine bị hủy: ${tokens.accessToken}")
            Log.w("MainActivity", "Coroutine bị hủy: ${tokens.refreshToken}")
            true
        }
    }


    override fun initData() {
        super.initData()
        subscribeEventNetwork { online ->
            runOnUiThread {
                binding.layoutNoInternet.root.isVisible = online.not()
            }
        }
        binding.layoutNoInternet.buttonSetting.singleClick { openSettingNetWork() }

        viewModel.isLoading.observe {
            binding.loading.loadingView.isVisible = it
        }
    }


    override fun onResume() {
        super.onResume()
        try {
            if (navController?.currentDestination == null) return
            AppMusicPlayer.checkAndPlay()

        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    override fun onPause() {
        super.onPause()
        AppMusicPlayer.stop()
        AppMusicPlayer.stopFxMusicPlayer()
    }

    override fun onDestroy() {
        AppMusicPlayer.releaseBackgroundMusic()
        AppMusicPlayer.releaseFxMusic()
        super.onDestroy()
    }


//    override fun attachBaseContext(context: Context) {
//        val locale = commonSharePref.languageCode ?: Language.ENGLISH.countryCode
//        val localeUpdatedContext: ContextWrapper =
//            ContextUtils.updateLocale(context, Locale(locale))
//        super.attachBaseContext(localeUpdatedContext)
//    }
}