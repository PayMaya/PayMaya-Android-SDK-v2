package com.paymaya.sdk.android.checkout.internal

import java.lang.Exception

internal sealed class ResponseWrapper

internal class SuccessResponse(
    val redirectUrl: String,
    val checkoutId: String
) : ResponseWrapper()

internal class ErrorResponse(
    val exception: Exception
) : ResponseWrapper()
