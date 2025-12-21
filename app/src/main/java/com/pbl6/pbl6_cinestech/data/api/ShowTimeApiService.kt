package com.pbl6.pbl6_cinestech.data.api

import com.pbl6.pbl6_cinestech.data.model.response.ItemWrapper
import com.pbl6.pbl6_cinestech.data.model.response.Response
import com.pbl6.pbl6_cinestech.data.model.response.ShowTimeResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ShowTimeApiService {
        @GET("api/v1/show-time/get-with-branch")
        suspend fun getShowTimeWithBranchAndMovie(
            @Query("movieId") movieId: String,
            @Query("branchId") branchId: String,
        ): Response<ItemWrapper<ShowTimeResponse>>

}