package com.pbl6.pbl6_cinestech.data.model.response

data class UserData(
    val id: String,
    val email: String,
    val status: String,
    val branchId: String?, // nullable
    val coin: Int,
    val phoneNumber: String?,
    val avatarUrl: String,
    val createdAt: String,
    val roleNames: List<String>,
)