package com.pbl6.pbl6_cinestech.data.api

import com.pbl6.pbl6_cinestech.data.model.response.BranchResponse
import com.pbl6.pbl6_cinestech.data.model.response.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface BranchApiService {
    @GET("/api/v1/branches")
    suspend fun getAllBranches(): Response<List<BranchResponse>>

    @GET("/api/v1/branches/{movieId}")
    suspend fun getBranchWithMovieId(@Path("movieId") movieId: String): Response<BranchResponse>

    @GET("/api/v1/branches/movies/{movieId}")
    suspend fun getBranchesWithMovieId(@Path("movieId") movieId: String): Response<List<BranchResponse>>
}