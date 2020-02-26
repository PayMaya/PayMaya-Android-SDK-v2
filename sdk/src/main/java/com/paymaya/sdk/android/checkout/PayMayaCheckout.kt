package com.paymaya.sdk.android.checkout

import android.app.Activity
import android.content.Intent
import com.paymaya.sdk.android.checkout.internal.CheckoutActivity
import com.paymaya.sdk.android.checkout.models.CheckoutRequest
import com.paymaya.sdk.android.common.LogLevel
import com.paymaya.sdk.android.common.PayMayaEnvironment
import com.paymaya.sdk.android.common.exceptions.BadRequestException
import com.paymaya.sdk.android.common.internal.Constants
import com.paymaya.sdk.android.common.internal.Constants.TAG
import com.paymaya.sdk.android.common.internal.di.CommonModule
import com.paymaya.sdk.android.common.internal.screen.PayMayaPaymentActivity

class PayMayaCheckout private constructor(
    private val clientKey: String,
    private val environment: PayMayaEnvironment,
    private val logLevel: LogLevel
) {

    private val logger = CommonModule.getLogger(logLevel)

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
            PayMayaCheckout(
                requireNotNull(clientKey),
                requireNotNull(environment),
                logLevel
            )
    }
}
