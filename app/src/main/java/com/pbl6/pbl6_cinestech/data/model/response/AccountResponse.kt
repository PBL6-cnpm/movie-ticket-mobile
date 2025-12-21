package com.pbl6.pbl6_cinestech.data.model.response

data class AccountResponse(
    var id: String,
    var fullName: String,
    var avatarUrl: String?,
    var email: String,
    var coin: Int,
    var phoneNumber: String?
) {
}