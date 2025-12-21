package com.pbl6.pbl6_cinestech.data.model.request

sealed class AuthEvent {
    object RequireLogin : AuthEvent()
}