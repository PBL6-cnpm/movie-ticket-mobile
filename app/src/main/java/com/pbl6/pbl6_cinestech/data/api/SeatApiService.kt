package com.pbl6.pbl6_cinestech.data.api

import com.pbl6.pbl6_cinestech.data.model.response.Response
import com.pbl6.pbl6_cinestech.data.model.response.SeatBookingResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface SeatApiService {
    @GET("/api/v1/seats/get-with-showtime/{showTimeId}")
    suspend fun getSeatsWithShowTime(
        @Path("showTimeId") showTimeId: String,
    ): Response<SeatBookingResponse>
}