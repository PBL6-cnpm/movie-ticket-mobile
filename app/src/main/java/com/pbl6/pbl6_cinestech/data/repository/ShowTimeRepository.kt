package com.pbl6.pbl6_cinestech.data.repository

import com.pbl6.pbl6_cinestech.data.api.ShowTimeApiService
import com.pbl6.pbl6_cinestech.data.model.response.ItemWrapper
import com.pbl6.pbl6_cinestech.data.model.response.Response
import com.pbl6.pbl6_cinestech.data.model.response.ShowTimeResponse

class ShowTimeRepository(
    private val showTimeApiService: ShowTimeApiService
) {
    suspend fun getShowTimeWithBranchAndMovie(movieId: String, branchId: String): Response<ItemWrapper<ShowTimeResponse>> {
        return showTimeApiService.getShowTimeWithBranchAndMovie(movieId, branchId)

    }
}