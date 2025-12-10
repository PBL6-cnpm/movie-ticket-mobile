package com.pbl6.pbl6_cinestech.data.model.request

class UpdateProfileRequest(
    var fullName: String,
    var avatarUrl: String?,
    var email: String,
    var phoneNumber: String
) {
}