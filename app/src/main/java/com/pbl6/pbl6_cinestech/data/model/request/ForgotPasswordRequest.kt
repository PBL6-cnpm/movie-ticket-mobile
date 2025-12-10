package com.pbl6.pbl6_cinestech.data.model.request

class ForgotPasswordRequest(
    val email: String
) {
    companion object {
        fun isValidEmail(email: String): Boolean {
            val emailRegex = Regex("^[A-Za-z0-9._%+-]+@gmail\\.com$")
            return emailRegex.matches(email)
        }
    }
}