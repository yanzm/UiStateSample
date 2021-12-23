package com.sample.uistatesample.data

sealed class ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Error(val e: Exception) : ApiResult<Nothing>()
}
