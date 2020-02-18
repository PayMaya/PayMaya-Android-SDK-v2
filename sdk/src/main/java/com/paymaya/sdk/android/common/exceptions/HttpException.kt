package com.paymaya.sdk.android.common.exceptions

class HttpException(
    val code: Int,
    message: String,
    cause: Throwable? = null
) : Exception(message, cause)
