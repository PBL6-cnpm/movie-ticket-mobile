package com.pbl6.pbl6_cinestech.data.model.request

class ApplyRefreshmentsRequest(
    var bookingId: String,
    var refreshmentsOption: List<Refreshments>? = null
) {
}