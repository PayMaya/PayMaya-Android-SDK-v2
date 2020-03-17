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

package com.paymaya.sdk.android.paywithpaymaya.internal

import android.app.Activity
import android.content.Intent
import com.paymaya.sdk.android.common.LogLevel
import com.paymaya.sdk.android.common.PayMayaEnvironment
import com.paymaya.sdk.android.common.exceptions.BadRequestException
import com.paymaya.sdk.android.common.internal.Constants
import com.paymaya.sdk.android.common.internal.Constants.TAG
import com.paymaya.sdk.android.common.internal.PayMayaClientBase
import com.paymaya.sdk.android.common.internal.di.CommonModule
import com.paymaya.sdk.android.common.internal.screen.PayMayaPaymentActivity
import com.paymaya.sdk.android.paywithpaymaya.CreateWalletLinkResult
import com.paymaya.sdk.android.paywithpaymaya.PayWithPayMaya
import com.paymaya.sdk.android.paywithpaymaya.PayWithPayMayaResult
import com.paymaya.sdk.android.paywithpaymaya.SinglePaymentResult
import com.paymaya.sdk.android.paywithpaymaya.models.CreateWalletLinkRequest
import com.paymaya.sdk.android.paywithpaymaya.models.SinglePaymentRequest

internal class PayWithPayMayaImpl internal constructor(
    clientPublicKey: String,
    environment: PayMayaEnvironment,
    logLevel: LogLevel
) : PayWithPayMaya, PayMayaClientBase(
    clientPublicKey,
    environment,
    logLevel,
    CommonModule.getCheckStatusUseCase(environment, clientPublicKey, logLevel)
) {

    private val logger = CommonModule.getLogger(logLevel)

    override fun startSinglePaymentActivityForResult(activity: Activity, request: SinglePaymentRequest) {
        val intent = SinglePaymentActivity.newIntent(
            activity,
            request,
            clientPublicKey,
            environment,
            logLevel
        )
        activity.startActivityForResult(intent, Constants.PAY_WITH_PAYMAYA_SINGLE_PAYMENT_REQUEST_CODE)
    }

    override fun startCreateWalletLinkActivityForResult(activity: Activity, request: CreateWalletLinkRequest) {
        val intent = CreateWalletLinkActivity.newIntent(
            activity,
            request,
            clientPublicKey,
            environment,
            logLevel
        )
        activity.startActivityForResult(intent, Constants.PAY_WITH_PAYMAYA_CREATE_WALLET_LINK_REQUEST_CODE)
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ): PayWithPayMayaResult? {
        when (requestCode) {
            Constants.PAY_WITH_PAYMAYA_SINGLE_PAYMENT_REQUEST_CODE -> {
                requireNotNull(data)
                val resultId = data.getStringExtra(PayMayaPaymentActivity.EXTRAS_RESULT_ID)

                return when (resultCode) {
                    Activity.RESULT_OK -> {
                        logger.i(TAG, "Pay With PayMaya result: OK")
                        requireNotNull(resultId)
                        SinglePaymentResult.Success(resultId)
                    }

                    Activity.RESULT_CANCELED -> {
                        logger.i(TAG, "Pay With PayMaya result: CANCELED")
                        SinglePaymentResult.Cancel(resultId)
                    }

                    PayMayaPaymentActivity.RESULT_FAILURE -> {
                        logger.e(TAG, "Pay With PayMaya result: FAILURE")
                        val exception =
                            data.getSerializableExtra(PayMayaPaymentActivity.EXTRAS_FAILURE_EXCEPTION) as Exception

                        if (exception is BadRequestException) {
                            logger.e(TAG, exception.error.toString())
                        } else {
                            logger.e(TAG, exception.toString())
                        }

                        SinglePaymentResult.Failure(resultId, exception)
                    }
                    else ->
                        throw IllegalStateException("Invalid result code: $resultCode")
                }
            }
            Constants.PAY_WITH_PAYMAYA_CREATE_WALLET_LINK_REQUEST_CODE -> {
                requireNotNull(data)
                val resultId = data.getStringExtra(PayMayaPaymentActivity.EXTRAS_RESULT_ID)

                return when (resultCode) {
                    Activity.RESULT_OK -> {
                        logger.i(TAG, "Pay With PayMaya result: OK")
                        requireNotNull(resultId)
                        CreateWalletLinkResult.Success(resultId)
                    }

                    Activity.RESULT_CANCELED -> {
                        logger.i(TAG, "Pay With PayMaya result: CANCELED")
                        CreateWalletLinkResult.Cancel(resultId)
                    }

                    PayMayaPaymentActivity.RESULT_FAILURE -> {
                        logger.e(TAG, "Pay With PayMaya result: FAILURE")

                        val exception =
                            data.getSerializableExtra(PayMayaPaymentActivity.EXTRAS_FAILURE_EXCEPTION) as Exception

                        if (exception is BadRequestException) {
                            logger.e(TAG, exception.error.toString())
                        }

                        CreateWalletLinkResult.Failure(resultId, exception)
                    }
                    else ->
                        throw IllegalStateException("Invalid result code: $resultCode")
                }
            }
            else -> return null
        }
    }

    class BuilderImpl : PayWithPayMaya.Builder {
        private var clientPublicKey: String? = null
        private var environment: PayMayaEnvironment? = null
        private var logLevel: LogLevel = LogLevel.WARN

        override fun clientPublicKey(value: String): BuilderImpl =
            apply { this.clientPublicKey = value }

        override fun environment(value: PayMayaEnvironment): BuilderImpl =
            apply { this.environment = value }

        override fun logLevel(value: LogLevel): BuilderImpl =
            apply { this.logLevel = value }

        override fun build(): PayWithPayMaya =
            PayWithPayMayaImpl(
                requireNotNull(clientPublicKey),
                requireNotNull(environment),
                logLevel
            )
    }
}
