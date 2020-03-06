package com.paymaya.sdk.android.vault

import android.app.Activity
import android.content.Intent
import androidx.annotation.DrawableRes
import com.paymaya.sdk.android.common.LogLevel
import com.paymaya.sdk.android.common.PayMayaClientBase
import com.paymaya.sdk.android.common.PayMayaEnvironment
import com.paymaya.sdk.android.common.internal.Constants
import com.paymaya.sdk.android.common.internal.Constants.TAG
import com.paymaya.sdk.android.common.internal.di.CommonModule
import com.paymaya.sdk.android.vault.internal.models.TokenizeCardResponse
import com.paymaya.sdk.android.vault.internal.screen.TokenizeCardActivity

/**
 * Main class to Pay With Vault Payment process. Allows to execute vault payment request. PayMayaVault class
 * takes client key and environments properties.
 *
 * @property clientKey Client key.
 * @property environment Property defining environment type.
 */
class PayMayaVault private constructor(
    clientKey: String,
    environment: PayMayaEnvironment,
    logLevel: LogLevel,
    @DrawableRes private val logoResId: Int?
) : PayMayaClientBase(
    clientKey,
    environment,
    logLevel,
    CommonModule.getCheckStatusUseCase(environment, clientKey, logLevel)
) {

    private val logger = CommonModule.getLogger(logLevel)

    /**
     * Function allowing to execute vault payment request with unnecessary data.
     *
     * @param activity Activity.
     */
    fun execute(activity: Activity) {
        val intent = TokenizeCardActivity.newIntent(
            activity,
            clientKey,
            environment,
            logLevel,
            logoResId
        )
        activity.startActivityForResult(intent, Constants.VAULT_CARD_FORM_REQUEST_CODE)
    }

    /**
     * Function checking which the request code should be answered. This function also extras result and for
     * appropriate result status return pay with vault result class.
     *
     * @return appropriate PayMayaVaultResult class object for success or cancel status
     */
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): PayMayaVaultResult? {
        if (requestCode == Constants.VAULT_CARD_FORM_REQUEST_CODE) {

            return when (resultCode) {
                Activity.RESULT_OK -> {
                    logger.i(TAG, "PayMay Vault result: OK")
                    requireNotNull(data)
                    val bundle = data.getBundleExtra(TokenizeCardActivity.EXTRAS_BUNDLE)
                    val result = bundle.getParcelable<TokenizeCardResponse>(TokenizeCardActivity.EXTRAS_RESULT)
                    requireNotNull(result)
                    PayMayaVaultResult.Success(
                        paymentTokenId = result.paymentTokenId,
                        state = result.state,
                        createdAt = result.createdAt,
                        updatedAt = result.updatedAt,
                        issuer = result.issuer
                    )
                }

                Activity.RESULT_CANCELED -> {
                    logger.i(TAG, "PayMay Vault result: CANCELED")
                    PayMayaVaultResult.Cancel
                }

                else ->
                    throw IllegalStateException("Invalid result code: $resultCode")
            }
        }

        return null
    }

    /**
     * Main class to PayMaya Vault process. Allows to execute pay with vault request with all data.
     *
     * @property clientKey Client key.
     * @property environment Property defining environment type.
     */
    class Builder(
        var clientKey: String? = null,
        var environment: PayMayaEnvironment? = null,
        var logLevel: LogLevel = LogLevel.WARN,
        var logoResId: Int? = null
    ) {
        /**
         * Function allowing to set client key
         *
         * @param value New client key.
         */
        fun clientKey(value: String) =
            apply { this.clientKey = value }

        /**
         * Function allowing to set environment type.
         *
         * @param value New environment type.
         */
        fun environment(value: PayMayaEnvironment) =
            apply { this.environment = value }

        /**
         * Function allowing to set log level.
         *
         * @param value New log level.
         */
        fun logLevel(value: LogLevel) =
            apply { this.logLevel = value }

        /**
         * Function allowing to build new PayMayaVault object.
         *
         * @return new PayMayaVault object.
         */
        fun build() =
            PayMayaVault(
                requireNotNull(clientKey),
                requireNotNull(environment),
                logLevel,
                logoResId
            )

        fun logo(@DrawableRes value: Int) =
            apply { this.logoResId = value }
    }
}
