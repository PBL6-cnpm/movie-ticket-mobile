package com.pbl6.pbl6_cinestech.data.api

import com.pbl6.pbl6_cinestech.data.model.request.ApplyRefreshmentsRequest
import com.pbl6.pbl6_cinestech.data.model.request.HoldingRequest
import com.pbl6.pbl6_cinestech.data.model.response.BookingResponse
import com.pbl6.pbl6_cinestech.data.model.response.HoldingSeatResponse
import com.pbl6.pbl6_cinestech.data.model.response.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface BookingApiService {
    @POST("/api/v1/bookings/hold/android-platform")
    suspend fun holdSeat(
        @Body bookingRequest : HoldingRequest
    ): Response<HoldingSeatResponse>

    @POST("/api/v1/bookings/apply-refreshments")
    suspend fun applyRefreshments(
        @Body bookingRequest: ApplyRefreshmentsRequest
    ): Response<BookingResponse>
}