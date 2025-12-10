package com.pbl6.pbl6_cinestech.data.api

import com.pbl6.pbl6_cinestech.data.model.request.ForgotPasswordRequest
import com.pbl6.pbl6_cinestech.data.model.request.LoginRequest
import com.pbl6.pbl6_cinestech.data.model.request.RefreshTokenRequest
import com.pbl6.pbl6_cinestech.data.model.request.RegisterRequest
import com.pbl6.pbl6_cinestech.data.model.response.LoginResponse
import com.pbl6.pbl6_cinestech.data.model.response.RefreshTokenResponse
import com.pbl6.pbl6_cinestech.data.model.response.Response
import com.pbl6.pbl6_cinestech.data.model.response.UserData
import com.pbl6.pbl6_cinestech.utils.NoAuth
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    @POST("/api/v1/auth/register")
    @NoAuth
    suspend fun register(@Body request: RegisterRequest): Response<UserData>

    @POST("/api/v1/auth/login")
    @NoAuth
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("/api/v1/auth/forgot-password")
    @NoAuth
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): Response<Nothing>

    @POST("/api/v1/auth/refresh-tokens")
    @NoAuth
    suspend fun refreshToken(@Body request: RefreshTokenRequest): Response<RefreshTokenResponse>
}