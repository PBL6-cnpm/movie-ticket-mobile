package com.pbl6.pbl6_cinestech.data.model.request

import com.google.gson.annotations.SerializedName

data class RefreshmentsOrder(
    var refreshmentId: String,
    var quantity: Int,
) {
}