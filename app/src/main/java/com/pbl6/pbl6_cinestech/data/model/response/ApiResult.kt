package com.pbl6.pbl6_cinestech.data.model.response

sealed class ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Error(val message: String?, val code: Int? = null) : ApiResult<Nothing>()
}