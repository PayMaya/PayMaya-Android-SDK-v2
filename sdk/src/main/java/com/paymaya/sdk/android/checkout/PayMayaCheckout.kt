package com.paymaya.sdk.android.checkout

import android.app.Activity
import android.content.Intent
import com.paymaya.sdk.android.checkout.internal.CheckoutActivity
import com.paymaya.sdk.android.checkout.models.CheckoutRequest
import com.paymaya.sdk.android.common.LogLevel
import com.paymaya.sdk.android.common.PayMayaClientBase
import com.paymaya.sdk.android.common.PayMayaEnvironment
import com.paymaya.sdk.android.common.exceptions.BadRequestException
import com.paymaya.sdk.android.common.internal.Constants
import com.paymaya.sdk.android.common.internal.Constants.TAG
import com.paymaya.sdk.android.common.internal.di.CommonModule
import com.paymaya.sdk.android.common.internal.screen.PayMayaPaymentActivity

/**
 * Main class to Checkout Payment process. Allows to execute checkout request with all data.
 *
 * @property clientKey Client key.
 * @property environment Property defining environment type.
 */
class PayMayaCheckout private constructor(
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
     * Function allowing to execute payment with checkout request data.
     *
     * @param activity Activity.
     * @param requestData Checkout request containing all information about checkout payment.
     */
    fun execute(activity: Activity, requestData: CheckoutRequest) {
        val intent = CheckoutActivity.newIntent(
            activity,
            requestData,
            clientKey,
            environment,
            logLevel
        )
        activity.startActivityForResult(intent, Constants.CHECKOUT_REQUEST_CODE)
    }

    /**
     * Function checking which the request code should be answered. This function also extras checkout id and for
     * appropriate result status return checkout results class.
     *
     * @return appropriate CheckoutResult class object for success, cancel or failure status
     */
    fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ): PayMayaCheckoutResult? {
        if (requestCode == Constants.CHECKOUT_REQUEST_CODE) {
            requireNotNull(data)
            val checkoutId = data.getStringExtra(PayMayaPaymentActivity.EXTRAS_RESULT_ID)

            return when (resultCode) {
                Activity.RESULT_OK -> {
                    logger.i(TAG, "PayMaya Checkout result: OK")
                    PayMayaCheckoutResult.Success(checkoutId)
                }

                Activity.RESULT_CANCELED -> {
                    logger.i(TAG, "PayMaya Checkout result: CANCELED")
                    PayMayaCheckoutResult.Cancel(checkoutId)
                }

                PayMayaPaymentActivity.RESULT_FAILURE -> {
                    logger.e(TAG, "PayMaya Checkout result: FAILURE")
                    val exception =
                        data.getSerializableExtra(PayMayaPaymentActivity.EXTRAS_FAILURE_EXCEPTION) as Exception

                    if (exception is BadRequestException) {
                        logger.e(TAG, exception.error.toString())
                    }

                    PayMayaCheckoutResult.Failure(checkoutId, exception)
                }
                else ->
                    throw IllegalStateException("Invalid result code: $resultCode")
            }
        }

        return null
    }

    /**
     * Main checkout builder class. Allows to set clients key, environment type, log level and
     * create new object of PayMayaCheckout class.
     *
     * @property clientKey Client key.
     * @property environment Property defining environment type.
     * @property logLevel Log type.
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
         * Function allowing to build new PayMayaCheckout object.
         *
         * @return new PayMayaCheckout object.
         */
        fun build() =
            PayMayaCheckout(
                requireNotNull(clientKey),
                requireNotNull(environment),
                logLevel
            )
    }
}
