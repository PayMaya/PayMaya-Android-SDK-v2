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
                    requireNotNull(bundle)
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
