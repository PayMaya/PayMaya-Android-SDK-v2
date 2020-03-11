/*
 * Copyright (c) 2020  PayMaya Philippines, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

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
