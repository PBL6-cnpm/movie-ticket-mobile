package com.pbl6.pbl6_cinestech.data.api

import com.pbl6.pbl6_cinestech.data.model.request.ChangePasswordRequest
import com.pbl6.pbl6_cinestech.data.model.request.UpdateProfileRequest
import com.pbl6.pbl6_cinestech.data.model.response.AccountResponse
import com.pbl6.pbl6_cinestech.data.model.response.Response
import retrofit2.http.Body
import retrofit2.http.PUT

interface ProfileApiService {
    @PUT("/api/v1/accounts/me")
    suspend fun updateProfile(@Body updateProfileRequest: UpdateProfileRequest): Response<AccountResponse>

    @PUT("/api/v1/accounts/me/passwords")
    suspend fun changePassword(
        @Body changePasswordRequest: ChangePasswordRequest
    ): Response<AccountResponse>

}