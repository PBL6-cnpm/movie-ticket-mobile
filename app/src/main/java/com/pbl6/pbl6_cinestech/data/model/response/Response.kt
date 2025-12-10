package com.pbl6.pbl6_cinestech.data.model.response

import androidx.annotation.Keep

@Keep
data class Response<T>(
    val success: Boolean,
    val statusCode: Int,
    val message: String,
    val code: String,
    val data: T?
)

@Keep
data class ItemWrapper<T>(
    val items: List<T>
)