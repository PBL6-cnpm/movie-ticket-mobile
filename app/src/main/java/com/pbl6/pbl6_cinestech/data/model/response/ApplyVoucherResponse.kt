package com.pbl6.pbl6_cinestech.data.model.response

data class ApplyVoucherResponse(
    val finalPrice: Int,
    val code: String,
    val price: Int,
    val voucherAmount: Int
) {
}