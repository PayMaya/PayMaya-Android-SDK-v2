package com.paymaya.sdk.android.checkout

import java.lang.Exception

/**
 * PayMayaCheckoutResult representing appropriate class for success, cancel or failure status
 */
sealed class PayMayaCheckoutResult {

    /**
     * Success class represent success status for checkout result and takes checkoutId property
     *
     * @property checkoutId Checkout id identifier.
     */
    class Success(
        val checkoutId: String
    ) : PayMayaCheckoutResult()

    /**
     * Cancel class represent cancel status for checkout result and takes checkoutId property
     *
     * @property checkoutId Checkout id identifier.
     */
    class Cancel(
        val checkoutId: String? = null
    ) : PayMayaCheckoutResult()

    /**
     * Failure class represent failure status for checkout result and takes checkoutId and exception properties
     *
     * @property checkoutId Checkout id identifier.
     * @property extension An exception that occurred when checkout result is failure.
     */
    class Failure(
        val checkoutId: String? = null,
        val exception: Exception
    ) : PayMayaCheckoutResult()
}
