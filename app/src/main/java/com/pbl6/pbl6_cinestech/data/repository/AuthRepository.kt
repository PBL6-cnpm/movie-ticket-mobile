package com.pbl6.pbl6_cinestech.data.repository

import android.util.Log
import com.pbl6.pbl6_cinestech.data.api.AuthApiService
import com.pbl6.pbl6_cinestech.data.model.request.ForgotPasswordRequest
import com.pbl6.pbl6_cinestech.data.model.request.GoogleLoginToken
import com.pbl6.pbl6_cinestech.data.model.request.LoginRequest
import com.pbl6.pbl6_cinestech.data.model.request.RegisterRequest
import com.pbl6.pbl6_cinestech.data.model.response.AccountResponse
import com.pbl6.pbl6_cinestech.data.model.response.LoginResponse
import com.pbl6.pbl6_cinestech.data.model.response.Response
import com.pbl6.pbl6_cinestech.data.model.response.UserData

class AuthRepository(
    private val authApiService: AuthApiService
) {
    suspend fun register(email: String, password: String, fullName: String): Response<UserData> {
        Log.d("LoginViewModel", "register: $email $password $fullName ${RegisterRequest.isValidPassword(password)}")
        require(RegisterRequest.isValidEmail(email)) { "Invalid email format" }
        require(RegisterRequest.isValidPassword(password)) { "Password must be at least 8 characters long and include at least one uppercase letter, one lowercase letter, one number, and one special character" }
        require(fullName.isNotBlank()) { "Full name cannot be blank" }
        val request = RegisterRequest(email, password, fullName, password)
        return authApiService.register(request)
    }

    suspend fun login(email: String, password: String): Response<LoginResponse> {
        val request = LoginRequest(email, password)
        return authApiService.login(request)
    }

    suspend fun loginWithGoogle(googleToken: GoogleLoginToken): Response<LoginResponse> {
        return authApiService.loginWithGoogle(googleToken)
    }

    suspend fun forgotPassword(email: String): Response<Nothing> {
        require(ForgotPasswordRequest.isValidEmail(email)) { "Invalid email" }
        val request = ForgotPasswordRequest(email)
        return authApiService.forgotPassword(request)
    }

    suspend fun getAccount(): Response<AccountResponse> {
        return authApiService.me()
    }
}