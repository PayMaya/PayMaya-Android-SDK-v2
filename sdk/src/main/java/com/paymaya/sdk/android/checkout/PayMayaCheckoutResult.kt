package com.paymaya.sdk.android.checkout

import java.lang.Exception

sealed class PayMayaCheckoutResult {

    class Success(
        val checkoutId: String
    ) : PayMayaCheckoutResult()

    class Cancel(
        val checkoutId: String? = null
    ) : PayMayaCheckoutResult()

    class Failure(
        val checkoutId: String? = null,
        val exception: Exception
    ) : PayMayaCheckoutResult()
}
