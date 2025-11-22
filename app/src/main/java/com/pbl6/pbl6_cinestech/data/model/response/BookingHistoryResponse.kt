package com.pbl6.pbl6_cinestech.data.model.response

import com.pbl6.pbl6_cinestech.data.model.request.Refreshments

class BookingHistoryResponse(
    var id: String,
    var status: String,
    var totalBookingPrice: Int,
    var dateTimeBooking: String,
    var checkInStatus: Boolean,
    var qrUrl: String?,
    var showTime: ShowTimeMovieResponse,
    var seats: List<Seat>,
    var refreshmentss: List<Refreshments>
) {
}