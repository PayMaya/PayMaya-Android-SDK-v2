package com.paymaya.sdk.android.checkout.exceptions

class HttpException(
    val code: Int,
    message: String,
    cause: Throwable? = null
) : Exception(message, cause)
