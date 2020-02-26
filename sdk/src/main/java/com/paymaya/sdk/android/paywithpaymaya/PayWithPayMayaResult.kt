package com.paymaya.sdk.android.paywithpaymaya

import java.lang.Exception


sealed class PayWithPayMayaResult

/**
 * SinglePaymentResult representing appropriate class for success, cancel or failure status
 */
sealed class SinglePaymentResult : PayWithPayMayaResult() {

    /**
     * Success class represent success status for single payment result. Takes paymentId property.
     *
     * @property paymentId Single payment id identifier.
     */
    class Success(
        val paymentId: String
    ) : SinglePaymentResult()

    /**
     * Cancel class represent cancel status for single payment result. Takes paymentId property.
     *
     * @property paymentId Single payment id identifier.
     */
    class Cancel(
        val paymentId: String? = null
    ) : SinglePaymentResult()

    /**
     * Failure class represent failure status for single payment result. Takes paymentId and exception properties.
     *
     * @property paymentId Single payment id identifier.
     * @property extension An exception that occurred when single payment result is failure.
     */
    class Failure(
        val paymentId: String? = null,
        val exception: Exception
    ) : SinglePaymentResult()
}

/**
 * SinglePaymentResult representing appropriate class for success, cancel or failure status
 */
sealed class CreateWalletLinkResult : PayWithPayMayaResult() {

    /**
     * Success class represent success status for create wallet link result. Takes linkId property.
     *
     * @property linkId Wallet link id identifier.
     */
    class Success(
        val linkId: String
    ) : CreateWalletLinkResult()

    /**
     * Cancel class represent cancel status for create wallet link result. Takes linkId property.
     *
     * @property linkId Wallet link id identifier.
     */
    class Cancel(
        val linkId: String? = null
    ) : CreateWalletLinkResult()

    /**
     * Failure class represent failure status for create wallet link result. Takes linkId and exception properties.
     *
     * @property linkId Wallet link id identifier.
     * @property extension An exception that occurred when create wallet link result is failure.
     */
    class Failure(
        val linkId: String? = null,
        val exception: Exception
    ) : CreateWalletLinkResult()
}
