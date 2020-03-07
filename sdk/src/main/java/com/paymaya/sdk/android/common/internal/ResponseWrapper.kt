package com.paymaya.sdk.android.common.internal

import com.paymaya.sdk.android.common.PaymentStatus

internal open class ResponseWrapper

internal open class SuccessResponseWrapper : ResponseWrapper()

internal class RedirectSuccessResponseWrapper(
    val resultId: String,
    val redirectUrl: String
) : SuccessResponseWrapper()

internal class StatusSuccessResponseWrapper(
    val id: String,
    val status: PaymentStatus
) : SuccessResponseWrapper()

internal class ErrorResponseWrapper(
    val exception: Exception
) : ResponseWrapper()
