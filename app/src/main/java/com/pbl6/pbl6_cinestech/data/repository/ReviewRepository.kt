package com.pbl6.pbl6_cinestech.data.repository

import com.pbl6.pbl6_cinestech.data.api.ReviewApiService
import com.pbl6.pbl6_cinestech.data.model.response.Response
import com.pbl6.pbl6_cinestech.data.model.response.ReviewResponse

class ReviewRepository(
    private val reviewApiService: ReviewApiService
) {
    suspend fun getLatestReviews(): Response<List<ReviewResponse>> {
        return reviewApiService.getLatestReviews()
    }
}