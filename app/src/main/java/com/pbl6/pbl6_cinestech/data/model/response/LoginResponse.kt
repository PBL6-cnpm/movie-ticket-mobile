package com.pbl6.pbl6_cinestech.data.model.response

class LoginResponse(
    val accessToken: String,
    val refreshToken: String,
    val account: UserData,
    val message: String
) {
}