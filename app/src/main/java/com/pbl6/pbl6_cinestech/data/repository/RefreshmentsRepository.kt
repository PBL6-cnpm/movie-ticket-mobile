package com.pbl6.pbl6_cinestech.data.repository

import com.pbl6.pbl6_cinestech.data.api.RefreshmentApiService
import com.pbl6.pbl6_cinestech.data.model.response.ItemWrapper
import com.pbl6.pbl6_cinestech.data.model.response.RefreshmentsResponse
import com.pbl6.pbl6_cinestech.data.model.response.Response

class RefreshmentsRepository(
    private val refreshmentsApiService: RefreshmentApiService
) {
    suspend fun getAllRefreshments(): Response<ItemWrapper<RefreshmentsResponse>> {
        return refreshmentsApiService.getAllRefreshments()

    }
}