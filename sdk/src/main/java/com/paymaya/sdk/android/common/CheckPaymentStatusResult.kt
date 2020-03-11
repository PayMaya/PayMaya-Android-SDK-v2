package com.paymaya.sdk.android.common

sealed class CheckPaymentStatusResult {

    /**
     * Checking payment status succeeded.
     *
     * @property status Payment status.
     */
    class Success internal constructor(
        val status: PaymentStatus
    ) : CheckPaymentStatusResult()

    /**
     * Checking payment status canceled.
     */
    object Cancel : CheckPaymentStatusResult()

    /**
     * Checking payment status failed.
     *
     * @property exception Exception with detailed reason of the failure.
     */
    class Failure internal constructor(
        val exception: Exception
    ) : CheckPaymentStatusResult()
}
