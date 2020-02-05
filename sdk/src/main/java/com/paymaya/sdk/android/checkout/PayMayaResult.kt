package com.paymaya.sdk.android.checkout

import java.lang.Exception

sealed class PayMayaResult

class ResultSuccess(
    val checkoutId: String
) : PayMayaResult()

class ResultCancel(
    val checkoutId: String? = null
) : PayMayaResult()

class ResultFailure(
    val checkoutId: String? = null,
    val exception: Exception
) : PayMayaResult()
