package com.pbl6.pbl6_cinestech.data.api

import com.pbl6.pbl6_cinestech.data.model.response.ItemWrapper
import com.pbl6.pbl6_cinestech.data.model.response.RefreshmentsResponse
import com.pbl6.pbl6_cinestech.data.model.response.Response
import retrofit2.http.GET

interface RefreshmentApiService {
    @GET("/api/v1/refreshments")
    suspend fun getAllRefreshments(): Response<ItemWrapper<RefreshmentsResponse>>
}