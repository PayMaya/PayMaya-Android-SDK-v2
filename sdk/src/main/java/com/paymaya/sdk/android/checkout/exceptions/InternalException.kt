package com.paymaya.sdk.android.checkout.exceptions

class InternalException(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause)
