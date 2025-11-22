package com.pbl6.pbl6_cinestech.data.api

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
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface BookingApiService {
    @POST("/api/v1/bookings/hold/android-platform")
    suspend fun holdSeat(
        @Body bookingRequest : HoldingRequest
    ): Response<HoldingSeatResponse>

    @POST("/api/v1/bookings/apply-refreshments")
    suspend fun applyRefreshments(
        @Body bookingRequest: ApplyRefreshmentsRequest
    ): Response<BookingResponse>

    @GET("api/v1/bookings")
    suspend fun getBookings(
        @Query("limit") limit: Int,
        @Query("offset") offset: Int
    ): Response<ItemWrapper<BookingHistoryResponse>>

    @POST("/api/v1/bookings/payment-confirmation-android")
    suspend fun bookingSeat(@Body bookingRequest: BookingRequest): Response<BookingSeatResponse>

    @GET("/api/v1/voucher/public")
    suspend fun getAllVoucher(): Response<List<VoucherResponse>>

    @POST("/api/v1/voucher/bookings/{bookingId}")
    suspend fun applyVoucher(@Path("bookingId") bookingId: String, @Body applyVoucherRequest: ApplyVoucherRequest): Response<ApplyVoucherResponse>
}