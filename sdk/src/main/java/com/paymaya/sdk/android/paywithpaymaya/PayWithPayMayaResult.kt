package com.paymaya.sdk.android.paywithpaymaya

import java.lang.Exception

sealed class PayWithPayMayaResult

sealed class SinglePaymentResult : PayWithPayMayaResult() {

    class Success(
        val paymentId: String
    ) : SinglePaymentResult()

    class Cancel(
        val paymentId: String? = null
    ) : SinglePaymentResult()

    class Failure(
        val paymentId: String? = null,
        val exception: Exception
    ) : SinglePaymentResult()
}

sealed class CreateWalletLinkResult : PayWithPayMayaResult() {

    class Success(
        val linkId: String
    ) : CreateWalletLinkResult()

    class Cancel(
        val linkId: String? = null
    ) : CreateWalletLinkResult()

    class Failure(
        val linkId: String? = null,
        val exception: Exception
    ) : CreateWalletLinkResult()
}
