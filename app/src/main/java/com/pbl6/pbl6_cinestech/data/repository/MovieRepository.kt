package com.pbl6.pbl6_cinestech.data.repository

import com.pbl6.pbl6_cinestech.data.api.MovieApiService
import com.pbl6.pbl6_cinestech.data.model.request.ReviewRequest
import com.pbl6.pbl6_cinestech.data.model.response.ItemWrapper
import com.pbl6.pbl6_cinestech.data.model.response.MovieResponse
import com.pbl6.pbl6_cinestech.data.model.response.Response
import com.pbl6.pbl6_cinestech.data.model.response.ReviewResponse

class MovieRepository(
    private val movieApiService: MovieApiService
) {
    suspend fun getAllMovieNowShowing(
        limit: Int,
        offset: Int
    ): Response<ItemWrapper<MovieResponse>> {
        return movieApiService.getAllMoviesShowing(limit, offset)
    }

    suspend fun getAllMovieUpcoming(limit: Int, offset: Int): Response<ItemWrapper<MovieResponse>> {
        return movieApiService.getAllMoviesUpcoming(limit, offset)
    }

    suspend fun getMovieDetails(movieId: String): Response<MovieResponse> {
        return movieApiService.getMovieDetail(movieId)
    }

    suspend fun getAllReview(movieId: String): Response<ItemWrapper<ReviewResponse>> {
        return movieApiService.getAllReview(movieId)
    }

    suspend fun deleteReview(movieId: String){
        return movieApiService.deleteReview(movieId)
    }

    suspend fun addReview(reviewRequest: ReviewRequest): Response<ReviewResponse> {
        return movieApiService.addReview(reviewRequest)
    }

}