package com.pbl6.pbl6_cinestech.data.model.request

data class RegisterRequest(
    val email: String,
    val password: String,
    val fullName: String,
    val confirmPassword: String
) {
    companion object{
        fun isValidEmail(email: String): Boolean {
            val emailRegex = Regex("^[A-Za-z0-9._%+-]+@gmail\\.com$")
            return emailRegex.matches(email)
        }
        fun isValidPassword(password: String): Boolean {
            if (password.length < 8) {
                return false
            }

            val hasLowerCase = password.any { it.isLowerCase() }
            if (!hasLowerCase) {
                return false
            }

            val hasUpperCase = password.any { it.isUpperCase() }
            if (!hasUpperCase) {
                return false
            }

            val hasDigit = password.any { it.isDigit() }
            if (!hasDigit) {
                return false
            }

            val specialChars = "!@#\$%^&*()_+-=[]{};':\"\\|,.<>/?`~"
            val hasSpecialChar = password.any { specialChars.contains(it) }
            return hasSpecialChar
        }

    }
}