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

    class Builder(
        var clientKey: String? = null,
        var environment: PayMayaEnvironment? = null,
        var logLevel: LogLevel = LogLevel.WARN,
        var logoResId: Int? = null
    ) {
        fun clientKey(value: String) =
            apply { this.clientKey = value }

        fun environment(value: PayMayaEnvironment) =
            apply { this.environment = value }

        fun logLevel(value: LogLevel) =
            apply { this.logLevel = value }

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
