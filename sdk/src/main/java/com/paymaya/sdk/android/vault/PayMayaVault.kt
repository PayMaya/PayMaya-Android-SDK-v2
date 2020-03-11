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
     * <p>
     * Use <code>onActivityResult</code> to get the result (<code>PayMayaVaultResult</code>).
     *
     * @param activity Current activity.
     */
    fun startTokenizeCardActivityForResult(activity: Activity)

    /**
     * Gets the result. Call it from your Activity's <code>onActivityResult</code>
     * to get the result of the token creation process.
     *
     * @return Returns non-null <code>PayMayaVaultResult</code> if the completed activity
     *         was the activity started by the <code>startTokenizeCardActivityForResult</code> method.
     */
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): PayMayaVaultResult?

    companion object {
        /**
         * Returns new PayMayaVault client builder.
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
         * Sets log level. See <code>LogLevel</code> for details. Optional.
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
