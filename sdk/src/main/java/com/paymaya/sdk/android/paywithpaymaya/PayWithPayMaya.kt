package com.paymaya.sdk.android.paywithpaymaya

import android.app.Activity
import android.content.Intent
import android.util.Log
import com.paymaya.sdk.android.common.PayMayaEnvironment
import com.paymaya.sdk.android.common.exceptions.BadRequestException
import com.paymaya.sdk.android.common.internal.Logger
import com.paymaya.sdk.android.common.internal.screen.PayMayaPaymentActivity
import com.paymaya.sdk.android.paywithpaymaya.internal.SinglePaymentActivity
import com.paymaya.sdk.android.paywithpaymaya.internal.CreateWalletLinkActivity
import com.paymaya.sdk.android.paywithpaymaya.models.CreateWalletLinkRequest
import com.paymaya.sdk.android.paywithpaymaya.models.SinglePaymentRequest

class PayWithPayMaya(
    private val clientKey: String,
    private val environment: PayMayaEnvironment,
    logLevel: Int
) {

    init {
        // TODO JIRA PS-16
        Logger.level = logLevel
    }

    fun executeSinglePayment(activity: Activity, requestData: SinglePaymentRequest) {
        val intent = SinglePaymentActivity.newIntent(
            activity,
            requestData,
            clientKey,
            environment
        )
        activity.startActivityForResult(intent, PAY_WITH_PAYMAYA_SINGLE_PAYMENT_REQUEST_CODE)
    }

    fun executeCreateWalletLink(activity: Activity, requestData: CreateWalletLinkRequest) {
        val intent = CreateWalletLinkActivity.newIntent(
            activity,
            requestData,
            clientKey,
            environment
        )
        activity.startActivityForResult(intent, PAY_WITH_PAYMAYA_CREATE_WALLET_LINK_REQUEST_CODE)
    }

    fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ): PayWithPayMayaResult? {
        when (requestCode) {
            PAY_WITH_PAYMAYA_SINGLE_PAYMENT_REQUEST_CODE -> {
                requireNotNull(data)
                val resultId = data.getStringExtra(PayMayaPaymentActivity.EXTRAS_RESULT_ID)

                return when (resultCode) {
                    Activity.RESULT_OK ->
                        SinglePaymentResult.Success(resultId)

                    Activity.RESULT_CANCELED ->
                        SinglePaymentResult.Cancel(resultId)

                    PayMayaPaymentActivity.RESULT_FAILURE -> {
                        val exception =
                            data.getSerializableExtra(PayMayaPaymentActivity.EXTRAS_FAILURE_EXCEPTION) as Exception

                        if (exception is BadRequestException) {
                            Logger.e(TAG, exception.error.toString())
                        }

                        SinglePaymentResult.Failure(resultId, exception)
                    }
                    else ->
                        throw IllegalStateException("Invalid result code: $resultCode")
                }
            }
            PAY_WITH_PAYMAYA_CREATE_WALLET_LINK_REQUEST_CODE -> {
                requireNotNull(data)
                val resultId = data.getStringExtra(PayMayaPaymentActivity.EXTRAS_RESULT_ID)

                return when (resultCode) {
                    Activity.RESULT_OK ->
                        CreateWalletLinkResult.Success(resultId)

                    Activity.RESULT_CANCELED ->
                        CreateWalletLinkResult.Cancel(resultId)

                    PayMayaPaymentActivity.RESULT_FAILURE -> {
                        val exception =
                            data.getSerializableExtra(PayMayaPaymentActivity.EXTRAS_FAILURE_EXCEPTION) as Exception

                        if (exception is BadRequestException) {
                            Logger.e(TAG, exception.error.toString())
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
        var logLevel: Int = Log.WARN
    ) {
        fun clientKey(value: String) =
            apply { this.clientKey = value }

        fun environment(value: PayMayaEnvironment) =
            apply { this.environment = value }

        fun logLevel(value: Int) =
            apply { this.logLevel = value }

        fun build() =
            PayWithPayMaya(
                requireNotNull(clientKey),
                requireNotNull(environment),
                logLevel
            )
    }

    companion object {
        private const val TAG = "PayWithPayMaya"
        private const val PAY_WITH_PAYMAYA_SINGLE_PAYMENT_REQUEST_CODE = 60708
        private const val PAY_WITH_PAYMAYA_CREATE_WALLET_LINK_REQUEST_CODE = 60709
    }
}
