package com.paymaya.sdk.android.common.internal

internal open class ResponseWrapper

internal open class SuccessResponseWrapper : ResponseWrapper()

internal class RedirectSuccessResponseWrapper(
    val resultId: String,
    val redirectUrl: String
) : SuccessResponseWrapper()

internal class ErrorResponseWrapper(
    val exception: Exception
) : ResponseWrapper()
