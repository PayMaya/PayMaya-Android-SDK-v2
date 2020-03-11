package com.paymaya.sdk.android.checkout.internal

import android.app.Activity
import android.content.Intent
import com.paymaya.sdk.android.checkout.PayMayaCheckout
import com.paymaya.sdk.android.checkout.PayMayaCheckoutResult
import com.paymaya.sdk.android.checkout.models.CheckoutRequest
import com.paymaya.sdk.android.common.LogLevel
import com.paymaya.sdk.android.common.PayMayaEnvironment
import com.paymaya.sdk.android.common.exceptions.BadRequestException
import com.paymaya.sdk.android.common.internal.Constants
import com.paymaya.sdk.android.common.internal.Constants.TAG
import com.paymaya.sdk.android.common.internal.PayMayaClientBase
import com.paymaya.sdk.android.common.internal.di.CommonModule
import com.paymaya.sdk.android.common.internal.screen.PayMayaPaymentActivity

internal class PayMayaCheckoutImpl constructor(
    clientPublicKey: String,
    environment: PayMayaEnvironment,
    logLevel: LogLevel
) : PayMayaCheckout, PayMayaClientBase(
    clientPublicKey,
    environment,
    logLevel,
    CommonModule.getCheckStatusUseCase(environment, clientPublicKey, logLevel)
) {

    private val logger = CommonModule.getLogger(logLevel)

    override fun startCheckoutActivityForResult(activity: Activity, request: CheckoutRequest) {
        val intent = CheckoutActivity.newIntent(
            activity,
            request,
            clientPublicKey,
            environment,
            logLevel
        )
        activity.startActivityForResult(intent, Constants.CHECKOUT_REQUEST_CODE)
    }

    override fun onActivityResult(
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

    class BuilderImpl : PayMayaCheckout.Builder {
        private var clientPublicKey: String? = null
        private var environment: PayMayaEnvironment? = null
        private var logLevel: LogLevel = LogLevel.WARN

        override fun clientPublicKey(value: String): BuilderImpl =
            apply { this.clientPublicKey = value }

        override fun environment(value: PayMayaEnvironment): BuilderImpl =
            apply { this.environment = value }

        override fun logLevel(value: LogLevel): BuilderImpl =
            apply { this.logLevel = value }

        override fun build(): PayMayaCheckout =
            PayMayaCheckoutImpl(
                requireNotNull(clientPublicKey),
                requireNotNull(environment),
                logLevel
            )
    }
}
