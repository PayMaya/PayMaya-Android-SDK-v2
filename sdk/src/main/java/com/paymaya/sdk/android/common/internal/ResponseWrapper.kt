package com.paymaya.sdk.android.common.internal

import java.lang.Exception

internal sealed class ResponseWrapper

internal class SuccessResponse(
    val responseId: String,
    val redirectUrl: String
) : ResponseWrapper()

internal class ErrorResponse(
    val exception: Exception
) : ResponseWrapper()
