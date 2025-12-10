package com.pbl6.pbl6_cinestech.data.model.request

import androidx.annotation.Keep

@Keep
class BookingRequest(
    var bookingId: String,
    var refreshmentsOption: List<RefreshmentsOrder> ,
    var voucherCode: String? = null
) {
}