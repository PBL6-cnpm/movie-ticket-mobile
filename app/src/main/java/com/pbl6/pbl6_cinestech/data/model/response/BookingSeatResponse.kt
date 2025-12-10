package com.pbl6.pbl6_cinestech.data.model.response

data class BookingSeatResponse(
    val bookingId: String,
    val totalPrice: Int,
    var status: String,
    var message: String
)
