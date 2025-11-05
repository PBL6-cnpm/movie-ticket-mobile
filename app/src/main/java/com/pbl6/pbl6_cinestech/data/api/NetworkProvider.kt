package com.pbl6.pbl6_cinestech.data.api

import android.app.Application
import android.content.Context
import com.pbl6.pbl6_cinestech.MainApplication
import com.pbl6.pbl6_cinestech.data.repository.AuthRepository
import com.pbl6.pbl6_cinestech.utils.AppConstants
import com.pbl6.pbl6_cinestech.utils.AuthInterceptor
import hoang.dqm.codebase.base.application.getBaseApplication
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

object NetworkProvider {
    private lateinit var applicationContext: Context

    fun init(appContext: Context) {
        applicationContext = appContext.applicationContext
    }
    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(applicationContext))
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }



    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(AppConstants.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }



    val movieApiService: MovieApiService by lazy {
        retrofit.create(MovieApiService::class.java)
    }

    val reviewApiService: ReviewApiService by lazy {
        retrofit.create(ReviewApiService::class.java)
    }

    val authApiService: AuthApiService by lazy {
        retrofit.create(AuthApiService::class.java)
    }

    val branchApiService: BranchApiService by lazy {
        retrofit.create(BranchApiService::class.java)
    }

    val showTimeApiService: ShowTimeApiService by lazy {
        retrofit.create(ShowTimeApiService::class.java)
    }

    val seatApiService: SeatApiService by lazy {
        retrofit.create(SeatApiService::class.java)
    }

    val refreshmentsApiService: RefreshmentApiService by lazy {
        retrofit.create(RefreshmentApiService::class.java)
    }
}
