package com.paymaya.sdk.android.checkout

sealed class PayMayaCheckoutResult {

    /**
     * Class representing success status of the Checkout payment.
     *
     * @property checkoutId Checkout id identifier.
     */
    class Success internal constructor(
        val checkoutId: String
    ) : PayMayaCheckoutResult()

    /**
     * Class representing canceled status of the Checkout payment.
     *
     * @property checkoutId Checkout id identifier if available or null.
     */
    class Cancel internal constructor(
        val checkoutId: String? = null
    ) : PayMayaCheckoutResult()

    /**
     * Failure class representing failure status of the Checkout payment.
     *
     * @property checkoutId Checkout id identifier if available or null.
     * @property exception Exception with detailed reason of the failure.
     */
    class Failure internal constructor(
        val checkoutId: String? = null,
        val exception: Exception
    ) : PayMayaCheckoutResult()
}
