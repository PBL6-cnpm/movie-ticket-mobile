package com.pbl6.pbl6_cinestech.data.model.response

class VoucherResponse(
    var id: String,
    var name: String,
    var code: String,
    var discountPercent: Int?,
    var maxDiscountValue: Int?,
    var discountValue: Int?,
    var minimumOrderValue: Int?
) {
}