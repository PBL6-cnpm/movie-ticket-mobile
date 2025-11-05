package com.pbl6.pbl6_cinestech.data.repository

import com.pbl6.pbl6_cinestech.data.api.AuthApiService
import com.pbl6.pbl6_cinestech.data.model.request.ForgotPasswordRequest
import com.pbl6.pbl6_cinestech.data.model.request.LoginRequest
import com.pbl6.pbl6_cinestech.data.model.request.RegisterRequest
import com.pbl6.pbl6_cinestech.data.model.response.LoginResponse
import com.pbl6.pbl6_cinestech.data.model.response.Response
import com.pbl6.pbl6_cinestech.data.model.response.UserData
import javax.inject.Inject
import javax.inject.Singleton

class AuthRepository(
    private val authApiService: AuthApiService
) {
    suspend fun register(email: String, password: String, fullName: String): Response<UserData> {
        require(RegisterRequest.isValidEmail(email)) { "Invalid email" }
        require(RegisterRequest.isValidPassword(password)) { "Invalid password" }
        require(fullName.isNotBlank()) { "Full name cannot be blank" }
        val request = RegisterRequest(email, password, fullName)
        return authApiService.register(request)
    }

    suspend fun login(email: String, password: String): Response<LoginResponse> {
        val request = LoginRequest(email, password)
        return authApiService.login(request)
    }

    suspend fun forgotPassword(email: String): Response<Nothing> {
        require(ForgotPasswordRequest.isValidEmail(email)) { "Invalid email" }
        val request = ForgotPasswordRequest(email)
        return authApiService.forgotPassword(request)
    }
}