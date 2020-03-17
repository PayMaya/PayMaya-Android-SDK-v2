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

package com.paymaya.sdk.android.vault

import android.app.Activity
import android.content.Intent
import androidx.annotation.DrawableRes
import com.paymaya.sdk.android.common.LogLevel
import com.paymaya.sdk.android.common.PayMayaEnvironment
import com.paymaya.sdk.android.vault.internal.PayMayaVaultImpl

/**
 * PayMaya Payment Vault client.
 */
interface PayMayaVault {

    /**
     * Initiates the tokenize card flow. Allows to create a payment token that
     * represents your customer’s credit or debit card details which can be
     * used for payments and customer card addition. The payment token is valid
     * for a specific amount of time. Before it expires, it is valid for single
     * use only in payment transactions.
     * Use [onActivityResult] to get the result ([PayMayaVaultResult]).
     *
     * @param activity Current activity.
     */
    fun startTokenizeCardActivityForResult(activity: Activity)

    /**
     * Gets the result. Call it from your Activity's [Activity.onActivityResult]
     * to get the result of the token creation process.
     *
     * @return Returns non-null [PayMayaVaultResult] if the completed activity
     *         was the activity started by the [startTokenizeCardActivityForResult] method.
     */
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): PayMayaVaultResult?

    companion object {
        /**
         * Returns new PayMayaVault client [Builder].
         */
        fun newBuilder(): Builder =
            PayMayaVaultImpl.BuilderImpl()
    }

    /**
     * PayMayaVault client builder.
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
         * Sets custom logo. Styles can be also used to customize logo and other elements.
         * Optional.
         */
        fun logo(@DrawableRes value: Int): Builder

        /**
         * Builds PayMayaVault client.
         */
        fun build(): PayMayaVault
    }
}
