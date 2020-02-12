package com.paymaya.sdk.android.common.exceptions

class InternalException(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause)
