package com.pbl6.pbl6_cinestech.data.repository

import com.pbl6.pbl6_cinestech.data.api.BookingApiService
import com.pbl6.pbl6_cinestech.data.model.request.ApplyRefreshmentsRequest
import com.pbl6.pbl6_cinestech.data.model.request.HoldingRequest
import com.pbl6.pbl6_cinestech.data.model.response.BookingResponse
import com.pbl6.pbl6_cinestech.data.model.response.HoldingSeatResponse
import com.pbl6.pbl6_cinestech.data.model.response.Response

class BookingRepository(
    private val bookingApiService: BookingApiService
) {
    suspend fun holdSeat(bookingRequest: HoldingRequest): Response<HoldingSeatResponse>{
        return bookingApiService.holdSeat(bookingRequest)
    }

    suspend fun applyRefreshments(bookingRequest: ApplyRefreshmentsRequest): Response<BookingResponse>{
        return bookingApiService.applyRefreshments(bookingRequest)
    }
}