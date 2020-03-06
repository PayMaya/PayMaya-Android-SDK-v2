package com.paymaya.sdk.android.paywithpaymaya

import android.app.Activity
import android.content.Intent
import com.paymaya.sdk.android.common.LogLevel
import com.paymaya.sdk.android.common.PayMayaClientBase
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

/**
 * Main class to Pay With PayMaya Payment process. Allows to execute requests such as create wallet link
 * and single payment. PayWithPayMaya class takes client key and environments properties.
 *
 * @property clientKey Client key.
 * @property environment Property defining environment type.
 */
class PayWithPayMaya private constructor(
    clientKey: String,
    environment: PayMayaEnvironment,
    logLevel: LogLevel
) : PayMayaClientBase(
    clientKey,
    environment,
    logLevel,
    CommonModule.getCheckStatusUseCase(environment, clientKey, logLevel)
) {

    private val logger = CommonModule.getLogger(logLevel)

    /**
     * Function allowing to execute single payment request with unnecessary data.
     *
     * @param activity Activity.
     * @param requestData Model of single payment request which contains all information about single payment.
     */
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

    /**
     * Function allowing to execute create wallet link request with unnecessary data.
     *
     * @param activity Activity.
     * @param requestData Model of create wallet link which contains all unnecessary information.
     */
    fun executeCreateWalletLink(activity: Activity, requestData: CreateWalletLinkRequest) {
        val intent = CreateWalletLinkActivity.newIntent(
            activity,
            requestData,
            clientKey,
            environment,
            logLevel
        )
        activity.startActivityForResult(intent, Constants.PAY_WITH_PAYMAYA_CREATE_WALLET_LINK_REQUEST_CODE)
    }

    /**
     * Function checking which the request code should be answered. This function also extras result id and for
     * appropriate result status return pay with paymaya result class.
     *
     * @return appropriate PayWithPayMayaResult class object for success, cancel or failure status
     */
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

    /**
     * Main class to Pay With PayMaya Payment process. Allows to execute pay with paymaya request with all data.
     *
     * @property clientKey Client key.
     * @property environment Property defining environment type.
     */
    data class Builder(
        var clientKey: String? = null,
        var environment: PayMayaEnvironment? = null,
        var logLevel: LogLevel = LogLevel.WARN
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
         * Function allowing to build new PayWithPayMaya object.
         *
         * @return new PayWithPayMaya object.
         */
        fun build() =
            PayWithPayMaya(
                requireNotNull(clientKey),
                requireNotNull(environment),
                logLevel
            )
    }
}
