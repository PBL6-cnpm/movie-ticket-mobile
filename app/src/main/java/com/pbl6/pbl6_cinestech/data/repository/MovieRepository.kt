package com.pbl6.pbl6_cinestech.data.repository

import com.pbl6.pbl6_cinestech.data.api.MovieApiService
import com.pbl6.pbl6_cinestech.data.model.response.ItemWrapper
import com.pbl6.pbl6_cinestech.data.model.response.MovieResponse
import com.pbl6.pbl6_cinestech.data.model.response.Response

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
}