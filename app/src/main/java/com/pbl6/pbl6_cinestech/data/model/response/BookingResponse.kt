package com.pbl6.pbl6_cinestech.data.model.response

data class BookingResponse(
    val id: String,
    val accountId: String,
    val voucherId: String?,
    val showTimeId: String,
    val paymentIntentId: String?,
    val status: String,
    val totalBookingPrice: Int,
    val dateTimeBooking: String,
    val createdAt: String,
    val updatedAt: String
)
