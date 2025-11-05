package com.pbl6.pbl6_cinestech.data.repository

import com.pbl6.pbl6_cinestech.data.api.BranchApiService
import com.pbl6.pbl6_cinestech.data.model.response.BranchResponse
import com.pbl6.pbl6_cinestech.data.model.response.Response

class BranchRepository(
    private val branchApiService: BranchApiService
) {
    suspend fun getAllBranch(): Response<List<BranchResponse>> {
        return branchApiService.getAllBranches()
    }

    suspend fun getBranchWithMovieId(movieId: String): Response<BranchResponse> {
        return branchApiService.getBranchWithMovieId(movieId)
    }
}

