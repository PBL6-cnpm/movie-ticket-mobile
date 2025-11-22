package com.pbl6.pbl6_cinestech.data.repository

import com.pbl6.pbl6_cinestech.data.api.BookingApiService
import com.pbl6.pbl6_cinestech.data.model.request.ApplyRefreshmentsRequest
import com.pbl6.pbl6_cinestech.data.model.request.ApplyVoucherRequest
import com.pbl6.pbl6_cinestech.data.model.request.BookingRequest
import com.pbl6.pbl6_cinestech.data.model.request.HoldingRequest
import com.pbl6.pbl6_cinestech.data.model.response.ApplyVoucherResponse
import com.pbl6.pbl6_cinestech.data.model.response.BookingHistoryResponse
import com.pbl6.pbl6_cinestech.data.model.response.BookingResponse
import com.pbl6.pbl6_cinestech.data.model.response.BookingSeatResponse
import com.pbl6.pbl6_cinestech.data.model.response.HoldingSeatResponse
import com.pbl6.pbl6_cinestech.data.model.response.ItemWrapper
import com.pbl6.pbl6_cinestech.data.model.response.Response
import com.pbl6.pbl6_cinestech.data.model.response.VoucherResponse

class BookingRepository(
    private val bookingApiService: BookingApiService
) {
    suspend fun holdSeat(bookingRequest: HoldingRequest): Response<HoldingSeatResponse>{
        return bookingApiService.holdSeat(bookingRequest)
    }

    suspend fun applyRefreshments(bookingRequest: ApplyRefreshmentsRequest): Response<BookingResponse>{
        return bookingApiService.applyRefreshments(bookingRequest)
    }

    suspend fun getBookings(limit: Int, offset: Int): Response<ItemWrapper<BookingHistoryResponse>>{
        return bookingApiService.getBookings(limit, offset)
    }

    suspend fun bookingSeat(bookingRequest: BookingRequest): Response<BookingSeatResponse>{
        return bookingApiService.bookingSeat(bookingRequest)
    }

    suspend fun getAllVoucher(): Response<List<VoucherResponse>>{
        return  bookingApiService.getAllVoucher()
    }

    suspend fun applyVoucher(applyVoucherRequest: ApplyVoucherRequest): Response<ApplyVoucherResponse> {
        return bookingApiService.applyVoucher(applyVoucherRequest.bookingId, applyVoucherRequest)
    }
}