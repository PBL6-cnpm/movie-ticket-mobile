package com.pbl6.pbl6_cinestech.data.model.request

data class ReviewRequest(
    val rating: Int,
    val comment:String,
    val movieId:String
) {
}