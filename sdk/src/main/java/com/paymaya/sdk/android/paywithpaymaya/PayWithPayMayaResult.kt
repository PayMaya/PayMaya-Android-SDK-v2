package com.paymaya.sdk.android.paywithpaymaya

sealed class PayWithPayMayaResult

/**
 * Result of the payment.
 */
sealed class SinglePaymentResult : PayWithPayMayaResult() {

    /**
     * Success result of the Single Payment.
     *
     * @property paymentId Payment identifier.
     */
    class Success internal constructor(
        val paymentId: String
    ) : SinglePaymentResult()

    /**
     * Canceled result of the Single Payment.
     *
     * @property paymentId Payment identifier.
     */
    class Cancel internal constructor(
        val paymentId: String? = null
    ) : SinglePaymentResult()

    /**
     * Failed result of the Single Payment.
     *
     * @property paymentId Payment identifier.
     * @property exception Exception with detailed reason of the failure.
     */
    class Failure internal constructor(
        val paymentId: String? = null,
        val exception: Exception
    ) : SinglePaymentResult()
}

/**
 * Result of the creation of the wallet link.
 */
sealed class CreateWalletLinkResult : PayWithPayMayaResult() {

    /**
     * Success result of the creation of the wallet link.
     *
     * @property linkId Wallet link identifier.
     */
    class Success internal constructor(
        val linkId: String
    ) : CreateWalletLinkResult()

    /**
     * Canceled result of the creation of the wallet link.
     *
     * @property linkId Wallet link id identifier.
     */
    class Cancel internal constructor(
        val linkId: String? = null
    ) : CreateWalletLinkResult()

    /**
     * Failed result of the creation of the wallet link.
     *
     * @property linkId Wallet link id identifier.
     * @property exception Exception with detailed reason of the failure.
     */
    class Failure internal constructor(
        val linkId: String? = null,
        val exception: Exception
    ) : CreateWalletLinkResult()
}
