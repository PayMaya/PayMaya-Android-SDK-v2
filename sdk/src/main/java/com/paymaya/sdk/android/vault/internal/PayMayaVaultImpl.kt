package com.paymaya.sdk.android.vault.internal

import android.app.Activity
import android.content.Intent
import androidx.annotation.DrawableRes
import com.paymaya.sdk.android.common.LogLevel
import com.paymaya.sdk.android.common.PayMayaEnvironment
import com.paymaya.sdk.android.common.internal.Constants
import com.paymaya.sdk.android.common.internal.Constants.TAG
import com.paymaya.sdk.android.common.internal.PayMayaClientBase
import com.paymaya.sdk.android.common.internal.di.CommonModule
import com.paymaya.sdk.android.vault.PayMayaVault
import com.paymaya.sdk.android.vault.PayMayaVaultResult
import com.paymaya.sdk.android.vault.internal.models.TokenizeCardResponse
import com.paymaya.sdk.android.vault.internal.screen.TokenizeCardActivity

internal class PayMayaVaultImpl internal constructor(
    clientPublicKey: String,
    environment: PayMayaEnvironment,
    logLevel: LogLevel,
    @DrawableRes private val logoResId: Int?
) : PayMayaVault, PayMayaClientBase(
    clientPublicKey,
    environment,
    logLevel,
    CommonModule.getCheckStatusUseCase(environment, clientPublicKey, logLevel)
) {

    private val logger = CommonModule.getLogger(logLevel)

    override fun startTokenizeCardActivityForResult(activity: Activity) {
        val intent = TokenizeCardActivity.newIntent(
            activity,
            clientPublicKey,
            environment,
            logLevel,
            logoResId
        )
        activity.startActivityForResult(intent, Constants.VAULT_CARD_FORM_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): PayMayaVaultResult? {
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

    class BuilderImpl : PayMayaVault.Builder {
        private var clientPublicKey: String? = null
        private var environment: PayMayaEnvironment? = null
        private var logLevel: LogLevel = LogLevel.WARN
        private var logoResId: Int? = null

        override fun clientPublicKey(value: String): BuilderImpl =
            apply { this.clientPublicKey = value }

        override fun environment(value: PayMayaEnvironment): BuilderImpl =
            apply { this.environment = value }

        override fun logLevel(value: LogLevel): BuilderImpl =
            apply { this.logLevel = value }

        override fun logo(@DrawableRes value: Int): BuilderImpl =
            apply { this.logoResId = value }

        override fun build(): PayMayaVault =
            PayMayaVaultImpl(
                requireNotNull(clientPublicKey),
                requireNotNull(environment),
                logLevel,
                logoResId
            )
    }
}
