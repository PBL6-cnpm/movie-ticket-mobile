package com.pbl6.pbl6_cinestech.utils

import android.content.Context
import com.pbl6.pbl6_cinestech.data.api.NetworkProvider
import com.pbl6.pbl6_cinestech.data.model.request.RefreshTokenRequest
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import retrofit2.Invocation

class AuthInterceptor(
    private val context: Context,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()

        val noAuth = request.tag(Invocation::class.java)
            ?.method()
            ?.getAnnotation(NoAuth::class.java) != null

        if (!noAuth) {
            val accessToken = SecurePrefs.getAccessToken(context)
            if (!accessToken.isNullOrEmpty()) {
                request = request.newBuilder()
                    .header("Authorization", "Bearer $accessToken")
                    .build()
            }
        }

        var response = chain.proceed(request)

        if (!noAuth && response.code == 401) {
            val newAccessToken = runBlocking { refreshToken() }
            if (!newAccessToken.isNullOrEmpty()) {
                response.close()
                val newRequest = request.newBuilder()
                    .header("Authorization", "Bearer $newAccessToken")
                    .build()
                response = chain.proceed(newRequest)
            }
        }

        return response
    }

    private suspend fun refreshToken(): String? {
        val refreshToken = SecurePrefs.getRefreshToken(context) ?: return null
        return try {
            val authApi = NetworkProvider.authApiService
            val response = authApi.refreshToken(RefreshTokenRequest(refreshToken))
            if (response.success) {
                val data = response.data
                if (data != null) {
                    SecurePrefs.saveTokens(context, data.accessToken, data.refreshToken)
                    data.accessToken
                } else null
            } else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}