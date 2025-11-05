package com.pbl6.pbl6_cinestech.data.repository

import com.pbl6.pbl6_cinestech.data.api.SeatApiService
import com.pbl6.pbl6_cinestech.data.model.response.Response
import com.pbl6.pbl6_cinestech.data.model.response.SeatBookingResponse

class SeatRepository(private var seatApiService: SeatApiService) {
    suspend fun getSeatWithShowTime(showTimeId: String): Response<SeatBookingResponse> {
        return seatApiService.getSeatsWithShowTime(showTimeId)
    }
}