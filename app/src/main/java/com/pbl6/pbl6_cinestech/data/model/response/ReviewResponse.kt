package com.pbl6.pbl6_cinestech.data.model.response

data class ReviewResponse(
    var rating: Int,
    var comment: String,
    var account: AccountResponse,
    var movie: MovieResponse,
    var createdAt: String,
    var updatedAt: String
) {
}