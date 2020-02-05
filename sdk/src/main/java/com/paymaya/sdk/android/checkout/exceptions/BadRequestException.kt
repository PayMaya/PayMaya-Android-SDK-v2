package com.paymaya.sdk.android.checkout.exceptions

import com.paymaya.sdk.android.checkout.models.PayMayaError

class BadRequestException(
    message: String,
    val payMayaError: PayMayaError
) : Exception(message)
