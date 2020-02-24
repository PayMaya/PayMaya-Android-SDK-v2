package com.paymaya.sdk.android.vault

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.annotation.DrawableRes
import com.paymaya.sdk.android.common.PayMayaEnvironment
import com.paymaya.sdk.android.common.internal.Constants
import com.paymaya.sdk.android.common.internal.Logger
import com.paymaya.sdk.android.vault.internal.models.TokenizeCardResponse
import com.paymaya.sdk.android.vault.internal.screen.TokenizeCardActivity

class PayMayaVault private constructor(
    private val clientKey: String,
    private val environment: PayMayaEnvironment,
    logLevel: Int,
    @DrawableRes private val logoResId: Int?
) {

    init {
        // TODO JIRA PS-16
        Logger.level = logLevel
    }

    fun execute(activity: Activity) {
        val intent = TokenizeCardActivity.newIntent(
            activity,
            clientKey,
            environment,
            logoResId
        )
        activity.startActivityForResult(intent, Constants.VAULT_CARD_FORM_REQUEST_CODE)
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): PayMayaVaultResult? {
        if (requestCode == Constants.VAULT_CARD_FORM_REQUEST_CODE) {

            return when (resultCode) {
                Activity.RESULT_OK -> {
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

                Activity.RESULT_CANCELED ->
                    PayMayaVaultResult.Cancel

                else ->
                    throw IllegalStateException("Invalid result code: $resultCode")
            }
        }

        return null
    }

    class Builder(
        var clientKey: String? = null,
        var environment: PayMayaEnvironment? = null,
        var logLevel: Int = Log.WARN,
        var logoResId: Int? = null
    ) {
        fun clientKey(value: String) =
            apply { this.clientKey = value }

        fun environment(value: PayMayaEnvironment) =
            apply { this.environment = value }

        fun logLevel(value: Int) =
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

    companion object {
        private const val TAG = "PayMayaVault"
    }
}
