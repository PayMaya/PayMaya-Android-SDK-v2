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

package com.paymaya.sdk.android.checkout

import android.app.Activity
import android.content.Intent
import com.paymaya.sdk.android.checkout.internal.PayMayaCheckoutImpl
import com.paymaya.sdk.android.checkout.models.CheckoutRequest
import com.paymaya.sdk.android.common.CheckPaymentStatusResult
import com.paymaya.sdk.android.common.LogLevel
import com.paymaya.sdk.android.common.PayMayaEnvironment

/**
 * Checkout client.
 */
interface PayMayaCheckout {

    /**
     * Initiates the checkout flow.
     * Use [onActivityResult] to get the result ([PayMayaCheckoutResult]).
     *
     * @param activity Current activity.
     * @param request Checkout request containing all information about
     *        the payment.
     */
    fun startCheckoutActivityForResult(activity: Activity, request: CheckoutRequest)

    /**
     * Gets the payment result. Call it from your Activity's [Activity.onActivityResult]
     * to get the result of the payment.
     *
     * @return Returns non-null [PayMayaCheckoutResult] if the completed activity
     *         was the activity started by the [startCheckoutActivityForResult] method.
     */
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): PayMayaCheckoutResult?

    /**
     * Checks status of the payment. The method is synchronous, don't call it from the Main thread.
     */
    fun checkPaymentStatus(id: String): CheckPaymentStatusResult

    companion object {
        /**
         * Returns new PayMayaCheckout client [Builder].
         */
        fun newBuilder(): Builder =
            PayMayaCheckoutImpl.BuilderImpl()
    }

    /**
     * Checkout client builder.
     */
    interface Builder {

        /**
         * Sets client public key. Required.
         */
        fun clientPublicKey(value: String): Builder

        /**
         * Sets environment type (sandbox or production). Required.
         */
        fun environment(value: PayMayaEnvironment): Builder

        /**
         * Sets log level. See [LogLevel] for details. Optional.
         */
        fun logLevel(value: LogLevel): Builder

        /**
         * Builds PayMayaCheckout client.
         */
        fun build(): PayMayaCheckout
    }
}
