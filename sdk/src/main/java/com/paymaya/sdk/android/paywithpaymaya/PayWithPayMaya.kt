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

import android.app.Activity
import android.content.Intent
import com.paymaya.sdk.android.common.CheckPaymentStatusResult
import com.paymaya.sdk.android.common.LogLevel
import com.paymaya.sdk.android.common.PayMayaEnvironment
import com.paymaya.sdk.android.paywithpaymaya.internal.PayWithPayMayaImpl
import com.paymaya.sdk.android.paywithpaymaya.models.CreateWalletLinkRequest
import com.paymaya.sdk.android.paywithpaymaya.models.SinglePaymentRequest

/**
 * PayWithPayMaya client.
 */
interface PayWithPayMaya {

    /**
     * Initiates the single payment flow.
     * <p>
     * Use <code>onActivityResult</code> to get the result (<code>SinglePaymentResult</code>).
     *
     * @param activity Current activity.
     * @param request SinglePaymentRequest request containing all information
     *        about the payment.
     */
    fun startSinglePaymentActivityForResult(activity: Activity, request: SinglePaymentRequest)

    /**
     * Initiates the create wallet link flow that allows charging PayMaya account.
     * <p>
     * Use <code>onActivityResult</code> to get the result (<code>CreateWalletLinkResult</code>).
     *
     * @param activity Current activity.
     * @param request <code>CreateWalletLinkRequest</code> request containing necessary information.
     */
    fun startCreateWalletLinkActivityForResult(activity: Activity, request: CreateWalletLinkRequest)

    /**
     * Gets the payment result. Call it from your Activity's <code>onActivityResult</code>
     * to get the result of the payment.
     *
     * @return Returns non-null <code>PayWithPayMayaResult</code> if the completed activity
     *         was the activity started by the <code>startSinglePaymentActivityForResult</code> or
     *         <code>startCreateWalletLinkActivityForResult</code> method.
     *         The result can be <code>SinglePaymentResult</code> or <code>CreateWalletLinkResult</code>.
     */
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): PayWithPayMayaResult?

    /**
     * Checks status of the payment. The method is synchronous, don't call it from the Main thread.
     */
    fun checkPaymentStatus(id: String): CheckPaymentStatusResult

    companion object {
        /**
         * Returns new PayWithPayMaya client builder.
         */
        fun newBuilder(): Builder =
            PayWithPayMayaImpl.BuilderImpl()
    }

    /**
     * PayWithPayMaya client builder.
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
         * Sets log level. See <code>LogLevel</code> for details. Optional.
         */
        fun logLevel(value: LogLevel): Builder

        /**
         * Builds PayWithPayMaya client.
         */
        fun build(): PayWithPayMaya
    }
}
