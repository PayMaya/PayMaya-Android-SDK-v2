package com.paymaya.sdk.android.paywithpaymaya

import android.app.Activity
import android.content.Intent
import com.paymaya.sdk.android.common.LogLevel
import com.paymaya.sdk.android.common.PayMayaEnvironment
import com.paymaya.sdk.android.common.exceptions.BadRequestException
import com.paymaya.sdk.android.common.internal.Constants
import com.paymaya.sdk.android.common.internal.Constants.TAG
import com.paymaya.sdk.android.common.internal.di.CommonModule
import com.paymaya.sdk.android.common.internal.screen.PayMayaPaymentActivity
import com.paymaya.sdk.android.paywithpaymaya.internal.CreateWalletLinkActivity
import com.paymaya.sdk.android.paywithpaymaya.internal.SinglePaymentActivity
import com.paymaya.sdk.android.paywithpaymaya.models.CreateWalletLinkRequest
import com.paymaya.sdk.android.paywithpaymaya.models.SinglePaymentRequest

class PayWithPayMaya(
    private val clientKey: String,
    private val environment: PayMayaEnvironment,
    private val logLevel: LogLevel
) {

    private val logger = CommonModule.getLogger(logLevel)

    fun executeSinglePayment(activity: Activity, requestData: SinglePaymentRequest) {
        val intent = SinglePaymentActivity.newIntent(
            activity,
            requestData,
            clientKey,
            environment,
            logLevel
        )
        activity.startActivityForResult(intent, Constants.PAY_WITH_PAYMAYA_SINGLE_PAYMENT_REQUEST_CODE)
    }

    fun executeCreateWalletLink(activity: Activity, requestData: CreateWalletLinkRequest) {
        val intent = CreateWalletLinkActivity.newIntent(
            activity,
            requestData,
            clientKey,
            environment
        )
        activity.startActivityForResult(intent, Constants.PAY_WITH_PAYMAYA_CREATE_WALLET_LINK_REQUEST_CODE)
    }

    fun onActivityResult(
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

    data class Builder(
        var clientKey: String? = null,
        var environment: PayMayaEnvironment? = null,
        var logLevel: LogLevel = LogLevel.WARN
    ) {
        fun clientKey(value: String) =
            apply { this.clientKey = value }

        fun environment(value: PayMayaEnvironment) =
            apply { this.environment = value }

        fun logLevel(value: LogLevel) =
            apply { this.logLevel = value }

        fun build() =
            PayWithPayMaya(
                requireNotNull(clientKey),
                requireNotNull(environment),
                logLevel
            )
    }
}
