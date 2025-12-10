package com.pbl6.pbl6_cinestech.data.api

import com.pbl6.pbl6_cinestech.data.model.response.Response
import com.pbl6.pbl6_cinestech.data.model.response.ReviewResponse
import com.pbl6.pbl6_cinestech.utils.NoAuth
import retrofit2.http.GET

interface ReviewApiService {
    @GET("/api/v1/reviews/latest")
    @NoAuth
    suspend fun getLatestReviews(): Response<List<ReviewResponse>>
}