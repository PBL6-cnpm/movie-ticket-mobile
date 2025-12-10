package com.pbl6.pbl6_cinestech.data.repository

import com.pbl6.pbl6_cinestech.data.api.ProfileApiService
import com.pbl6.pbl6_cinestech.data.model.request.ChangePasswordRequest
import com.pbl6.pbl6_cinestech.data.model.request.UpdateProfileRequest
import com.pbl6.pbl6_cinestech.data.model.response.AccountResponse
import com.pbl6.pbl6_cinestech.data.model.response.Response

class ProfileRepository(
    private val profileApiService: ProfileApiService,
) {
    suspend fun updateProfile(updateProfileRequest: UpdateProfileRequest): Response<AccountResponse> {
        return profileApiService.updateProfile(updateProfileRequest)
    }

    suspend fun updatePassword(passwordRequest: ChangePasswordRequest): Response<AccountResponse> {
        return profileApiService.changePassword(passwordRequest)
    }

}