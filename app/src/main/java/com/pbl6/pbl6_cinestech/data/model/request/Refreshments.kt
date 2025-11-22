package com.pbl6.pbl6_cinestech.data.model.request

import com.google.gson.annotations.SerializedName

class Refreshments(
    var refreshmentId: String,
    var quantity: Int,
    var price:Int = 0,
    var name: String = "",
    var imgPath: String = ""
) {
}