package com.paymaya.sdk.android.checkout

import android.app.Activity
import android.content.Intent
import android.util.Log
import com.paymaya.sdk.android.common.PayMayaEnvironment
import com.paymaya.sdk.android.common.exceptions.BadRequestException
import com.paymaya.sdk.android.checkout.internal.CheckoutActivity
import com.paymaya.sdk.android.checkout.models.CheckoutRequest
import com.paymaya.sdk.android.common.internal.Logger
import com.paymaya.sdk.android.common.internal.screen.PayMayaPaymentActivity

class PayMayaCheckout private constructor(
    private val clientKey: String,
    private val environment: PayMayaEnvironment,
    logLevel: Int
) {

    init {
        // TODO JIRA PS-16
        Logger.level = logLevel
    }

    fun execute(activity: Activity, requestData: CheckoutRequest) {
        val intent = CheckoutActivity.newIntent(
            activity,
            requestData,
            clientKey,
            environment
        )
        activity.startActivityForResult(intent, CHECKOUT_REQUEST_CODE)
    }

    fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ): PayMayaCheckoutResult? {
        if (requestCode == CHECKOUT_REQUEST_CODE) {
            requireNotNull(data)
            val checkoutId = data.getStringExtra(PayMayaPaymentActivity.EXTRAS_RESULT_ID)

            return when (resultCode) {
                Activity.RESULT_OK ->
                    PayMayaCheckoutResult.Success(checkoutId)

                Activity.RESULT_CANCELED ->
                    PayMayaCheckoutResult.Cancel(checkoutId)

                PayMayaPaymentActivity.RESULT_FAILURE -> {
                    val exception =
                        data.getSerializableExtra(PayMayaPaymentActivity.EXTRAS_FAILURE_EXCEPTION) as Exception

                    if (exception is BadRequestException) {
                        Logger.e(TAG, exception.error.toString())
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
        var logLevel: Int = Log.WARN
    ) {
        fun clientKey(value: String) =
            apply { this.clientKey = value }

        fun environment(value: PayMayaEnvironment) =
            apply { this.environment = value }

        fun logLevel(value: Int) =
            apply { this.logLevel = value }

        fun build() =
            PayMayaCheckout(
                requireNotNull(clientKey),
                requireNotNull(environment),
                logLevel
            )
    }

    companion object {
        private const val TAG = "PayMayaCheckout"
        private const val CHECKOUT_REQUEST_CODE = 70707
    }
}
