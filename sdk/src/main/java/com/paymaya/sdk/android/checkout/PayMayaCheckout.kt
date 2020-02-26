package com.paymaya.sdk.android.checkout

import android.app.Activity
import android.content.Intent
import android.util.Log
import com.paymaya.sdk.android.common.PayMayaEnvironment
import com.paymaya.sdk.android.common.exceptions.BadRequestException
import com.paymaya.sdk.android.checkout.internal.CheckoutActivity
import com.paymaya.sdk.android.checkout.models.CheckoutRequest
import com.paymaya.sdk.android.common.internal.Constants
import com.paymaya.sdk.android.common.internal.Logger
import com.paymaya.sdk.android.common.internal.screen.PayMayaPaymentActivity

/**
 * Main class to Checkout Payment process. Allows to execute checkout request with all data.
 *
 * @property clientKey Client key.
 * @property environment Property defining environment type.
 */
class PayMayaCheckout private constructor(
    private val clientKey: String,
    private val environment: PayMayaEnvironment,
    logLevel: Int
) {

    init {
        // TODO JIRA PS-16
        Logger.level = logLevel
    }

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
            environment
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
        var logLevel: Int = Log.WARN
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
        fun logLevel(value: Int) =
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

    companion object {
        private const val TAG = "PayMayaCheckout"
    }
}
