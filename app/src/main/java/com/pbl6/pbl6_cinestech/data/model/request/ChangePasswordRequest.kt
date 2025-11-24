package com.pbl6.pbl6_cinestech.data.model.request

class ChangePasswordRequest(
    var currentPassword: String,
    var newPassword: String,
    var confirmPassword: String
) {
}