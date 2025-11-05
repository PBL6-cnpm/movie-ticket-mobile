package com.pbl6.pbl6_cinestech.data.api

import com.pbl6.pbl6_cinestech.data.model.request.PageRequest
import com.pbl6.pbl6_cinestech.data.model.response.ItemWrapper
import com.pbl6.pbl6_cinestech.data.model.response.MovieResponse
import com.pbl6.pbl6_cinestech.data.model.response.Response
import com.pbl6.pbl6_cinestech.utils.NoAuth
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieApiService {
    @GET("/api/v1/movies/now-showing")
    @NoAuth
    suspend fun getAllMoviesShowing(
        @Query("limit") limit: Int,
        @Query("offset") offset: Int
    ): Response<ItemWrapper<MovieResponse>>

    @GET("/api/v1/movies/upcoming")
    @NoAuth
    suspend fun getAllMoviesUpcoming(
        @Query("limit") limit: Int,
        @Query("offset") offset: Int
    ): Response<ItemWrapper<MovieResponse>>

    @GET("/api/v1/movies/{id}")
    @NoAuth
    suspend fun getMovieDetail(
        @Path("id") movieId: String
    ): Response<MovieResponse>

}