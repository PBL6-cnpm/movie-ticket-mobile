package com.pbl6.pbl6_cinestech.data.model.request

data class RegisterRequest(
    val email: String,
    val password: String,
    val fullName: String
) {
    companion object{
        fun isValidEmail(email: String): Boolean {
            val emailRegex = Regex("^[A-Za-z0-9._%+-]+@gmail\\.com$")
            return emailRegex.matches(email)
        }
        fun isValidPassword(password: String): Boolean {
            val passwordRegex = Regex("^(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#\$%^&*()_+=\\-{}|:;\"'<>,.?/]).{8,}$")
            return passwordRegex.matches(password)
        }

    }
}